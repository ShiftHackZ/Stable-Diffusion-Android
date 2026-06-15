import Foundation
import MLX
import MLXNN

final class BonsaiFluxVAE {
    private let postQuantConv: VAEConv2D
    private let batchNormMean: MLXArray
    private let batchNormVar: MLXArray
    private let decoder: VAEDecoder
    private let eps: Float

    init(layout: BonsaiModelLayout) throws {
        let config = try BonsaiFluxVAEConfig.load(from: layout.vaeURL)
        let tensors = try BonsaiSafetensors(directoryURL: layout.vaeURL)
        postQuantConv = try VAEConv2D(prefix: "post_quant_conv", tensors: tensors)
        batchNormMean = try tensors.require("bn.running_mean")
        batchNormVar = try tensors.require("bn.running_var")
        decoder = try VAEDecoder(config: config, tensors: tensors)
        eps = config.batchNormEps
    }

    func decodePacked(_ packedLatents: MLXArray) -> MLXArray {
        let mean = batchNormMean.reshaped(1, -1, 1, 1)
        let std = sqrt(batchNormVar.reshaped(1, -1, 1, 1) + eps)
        let normalized = packedLatents * std + mean
        let latents = Self.unpatchify(normalized)
        return decoder(postQuantConv(latents))
    }

    private static func unpatchify(_ latents: MLXArray) -> MLXArray {
        let shape = latents.shape
        return latents
            .reshaped(shape[0], shape[1] / 4, 2, 2, shape[2], shape[3])
            .transposed(0, 1, 4, 2, 5, 3)
            .reshaped(shape[0], shape[1] / 4, shape[2] * 2, shape[3] * 2)
    }
}

private struct BonsaiFluxVAEConfig: Decodable {
    let blockOutChannels: [Int]
    let layersPerBlock: Int
    let normNumGroups: Int
    let batchNormEps: Float

    enum CodingKeys: String, CodingKey {
        case blockOutChannels = "block_out_channels"
        case layersPerBlock = "layers_per_block"
        case normNumGroups = "norm_num_groups"
        case batchNormEps = "batch_norm_eps"
    }

    static func load(from vaeURL: URL) throws -> BonsaiFluxVAEConfig {
        let url = vaeURL.appendingPathComponent("config.json", isDirectory: false)
        let data = try Data(contentsOf: url)
        let config = try JSONDecoder().decode(BonsaiFluxVAEConfig.self, from: data)
        guard config.blockOutChannels.count == 4 else {
            throw BonsaiRuntimeError.invalidModelLayout(
                "Flux2 VAE requires four block_out_channels, got \(config.blockOutChannels)"
            )
        }
        guard config.layersPerBlock > 0, config.normNumGroups > 0 else {
            throw BonsaiRuntimeError.invalidModelLayout("invalid Flux2 VAE decoder config")
        }
        return config
    }
}

private final class VAEDecoder {
    private let convIn: VAEConv2D
    private let midBlock: VAEMidBlock
    private let upBlocks: [VAEUpBlock]
    private let normOut: VAEGroupNorm
    private let convOut: VAEConv2D

    init(config: BonsaiFluxVAEConfig, tensors: BonsaiSafetensors) throws {
        let blockOutChannels = config.blockOutChannels
        let reversedChannels = Array(blockOutChannels.reversed())
        convIn = try VAEConv2D(prefix: "decoder.conv_in", tensors: tensors)
        midBlock = try VAEMidBlock(
            prefix: "decoder.mid_block",
            channels: blockOutChannels.last!,
            groupCount: config.normNumGroups,
            tensors: tensors
        )
        upBlocks = try reversedChannels.enumerated().map { index, outputChannel in
            let inputChannel = index == 0 ? outputChannel : reversedChannels[index - 1]
            return try VAEUpBlock(
                prefix: "decoder.up_blocks.\(index)",
                inChannels: inputChannel,
                outChannels: outputChannel,
                layerCount: config.layersPerBlock + 1,
                groupCount: config.normNumGroups,
                addUpsample: index != reversedChannels.count - 1,
                tensors: tensors
            )
        }
        normOut = try VAEGroupNorm(
            prefix: "decoder.conv_norm_out",
            channels: blockOutChannels[0],
            groupCount: config.normNumGroups,
            tensors: tensors
        )
        convOut = try VAEConv2D(prefix: "decoder.conv_out", tensors: tensors)
    }

    func callAsFunction(_ latents: MLXArray) -> MLXArray {
        var hidden = convIn(latents)
        eval(hidden)
        BonsaiMlxMemory.reclaimCache()
        hidden = midBlock(hidden)
        eval(hidden)
        BonsaiMlxMemory.reclaimCache()
        for block in upBlocks {
            hidden = block(hidden)
            eval(hidden)
            BonsaiMlxMemory.reclaimCache()
        }
        hidden = normOut(hidden)
        hidden = silu(hidden).asType(.bfloat16)
        return convOut(hidden)
    }
}

private final class VAEMidBlock {
    private let resnet0: VAEResnetBlock
    private let attention: VAEAttentionBlock
    private let resnet1: VAEResnetBlock

    init(prefix: String, channels: Int, groupCount: Int, tensors: BonsaiSafetensors) throws {
        resnet0 = try VAEResnetBlock(
            prefix: "\(prefix).resnets.0",
            inChannels: channels,
            outChannels: channels,
            groupCount: groupCount,
            tensors: tensors
        )
        attention = try VAEAttentionBlock(
            prefix: "\(prefix).attentions.0",
            channels: channels,
            groupCount: groupCount,
            tensors: tensors
        )
        resnet1 = try VAEResnetBlock(
            prefix: "\(prefix).resnets.1",
            inChannels: channels,
            outChannels: channels,
            groupCount: groupCount,
            tensors: tensors
        )
    }

    func callAsFunction(_ hidden: MLXArray) -> MLXArray {
        resnet1(attention(resnet0(hidden)))
    }
}

private final class VAEUpBlock {
    private let resnets: [VAEResnetBlock]
    private let upsample: VAEConv2D?

    init(
        prefix: String,
        inChannels: Int,
        outChannels: Int,
        layerCount: Int,
        groupCount: Int,
        addUpsample: Bool,
        tensors: BonsaiSafetensors
    ) throws {
        resnets = try (0..<layerCount).map { index in
            try VAEResnetBlock(
                prefix: "\(prefix).resnets.\(index)",
                inChannels: index == 0 ? inChannels : outChannels,
                outChannels: outChannels,
                groupCount: groupCount,
                tensors: tensors
            )
        }
        upsample = addUpsample ? try VAEConv2D(prefix: "\(prefix).upsamplers.0.conv", tensors: tensors) : nil
    }

    func callAsFunction(_ hidden: MLXArray) -> MLXArray {
        var output = hidden
        for resnet in resnets {
            output = resnet(output)
        }
        if let upsample {
            output = repeated(output, count: 2, axis: 2)
            output = repeated(output, count: 2, axis: 3)
            output = upsample(output)
        }
        return output
    }
}

private final class VAEResnetBlock {
    private let norm1: VAEGroupNorm
    private let conv1: VAEConv2D
    private let norm2: VAEGroupNorm
    private let conv2: VAEConv2D
    private let shortcut: VAEConv2D?

    init(prefix: String, inChannels: Int, outChannels: Int, groupCount: Int, tensors: BonsaiSafetensors) throws {
        norm1 = try VAEGroupNorm(prefix: "\(prefix).norm1", channels: inChannels, groupCount: groupCount, tensors: tensors)
        conv1 = try VAEConv2D(prefix: "\(prefix).conv1", tensors: tensors)
        norm2 = try VAEGroupNorm(prefix: "\(prefix).norm2", channels: outChannels, groupCount: groupCount, tensors: tensors)
        conv2 = try VAEConv2D(prefix: "\(prefix).conv2", tensors: tensors)
        if tensors["\(prefix).conv_shortcut.weight"] != nil {
            shortcut = try VAEConv2D(prefix: "\(prefix).conv_shortcut", tensors: tensors)
        } else {
            shortcut = nil
        }
    }

    func callAsFunction(_ hidden: MLXArray) -> MLXArray {
        var output = norm1(hidden).asType(.float32)
        output = silu(output).asType(.bfloat16)
        output = conv1(output)
        output = norm2(output).asType(.float32)
        output = silu(output).asType(.bfloat16)
        output = conv2(output)
        let residual = shortcut?(hidden) ?? hidden
        return output + residual
    }
}

private final class VAEAttentionBlock {
    private let norm: VAEGroupNorm
    private let toQ: BonsaiLinear
    private let toK: BonsaiLinear
    private let toV: BonsaiLinear
    private let toOut: BonsaiLinear
    private let channels: Int

    init(prefix: String, channels: Int, groupCount: Int, tensors: BonsaiSafetensors) throws {
        self.channels = channels
        norm = try VAEGroupNorm(prefix: "\(prefix).group_norm", channels: channels, groupCount: groupCount, tensors: tensors)
        toQ = try Self.linear(prefix: "\(prefix).to_q", tensors: tensors)
        toK = try Self.linear(prefix: "\(prefix).to_k", tensors: tensors)
        toV = try Self.linear(prefix: "\(prefix).to_v", tensors: tensors)
        toOut = try Self.linear(prefix: "\(prefix).to_out.0", fallbackPrefix: "\(prefix).to_out", tensors: tensors)
    }

    func callAsFunction(_ hidden: MLXArray) -> MLXArray {
        let nhwc = hidden.transposed(0, 2, 3, 1)
        let shape = nhwc.shape
        let normed = norm(hidden).transposed(0, 2, 3, 1)
        let query = toQ(normed).reshaped(shape[0], shape[1] * shape[2], 1, channels).transposed(0, 2, 1, 3)
        let key = toK(normed).reshaped(shape[0], shape[1] * shape[2], 1, channels).transposed(0, 2, 1, 3)
        let value = toV(normed).reshaped(shape[0], shape[1] * shape[2], 1, channels).transposed(0, 2, 1, 3)
        let attended = MLXFast.scaledDotProductAttention(
            queries: query.asType(.float32),
            keys: key.asType(.float32),
            values: value.asType(.float32),
            scale: 1.0 / Float(sqrt(Double(channels))),
            mask: nil
        )
            .asType(hidden.dtype)
            .transposed(0, 2, 1, 3)
            .reshaped(shape[0], shape[1], shape[2], channels)
        return (nhwc + toOut(attended)).transposed(0, 3, 1, 2)
    }

    private static func linear(prefix: String, fallbackPrefix: String? = nil, tensors: BonsaiSafetensors) throws -> BonsaiLinear {
        let preferred = "\(prefix).weight"
        let fallback = fallbackPrefix.map { "\($0).weight" }
        let key = tensors[preferred] != nil ? preferred : (fallback ?? preferred)
        return BonsaiLinear(
            dense: try tensors.require(key),
            bias: tensors.optional(key.replacingOccurrences(of: ".weight", with: ".bias"))
        )
    }
}

private struct VAEConv2D {
    private let weight: MLXArray
    private let bias: MLXArray?
    private let padding: Int

    init(prefix: String, tensors: BonsaiSafetensors) throws {
        let rawWeight = try tensors.require("\(prefix).weight")
        weight = rawWeight.transposed(0, 2, 3, 1).asType(.bfloat16)
        bias = tensors.optional("\(prefix).bias")
        self.padding = rawWeight.shape[2] == 1 ? 0 : 1
    }

    func callAsFunction(_ hidden: MLXArray) -> MLXArray {
        let nhwc = hidden.transposed(0, 2, 3, 1)
        var output = conv2d(nhwc, weight, stride: IntOrPair(1), padding: IntOrPair(padding))
        if let bias {
            output = output + bias
        }
        return output.transposed(0, 3, 1, 2)
    }
}

private struct VAEGroupNorm {
    private let groupCount: Int
    private let weight: MLXArray
    private let bias: MLXArray
    private let eps: Float = 1e-6

    init(prefix: String, channels: Int, groupCount: Int, tensors: BonsaiSafetensors) throws {
        let rawWeight = try tensors.require("\(prefix).weight", dtype: .float32)
        let rawBias = try tensors.require("\(prefix).bias", dtype: .float32)
        guard rawWeight.shape.reduce(1, *) == channels, rawBias.shape.reduce(1, *) == channels else {
            throw BonsaiRuntimeError.invalidModelLayout(
                "GroupNorm \(prefix) expected \(channels) channels, got weight \(rawWeight.shape) bias \(rawBias.shape)"
            )
        }
        guard channels % groupCount == 0 else {
            throw BonsaiRuntimeError.invalidModelLayout(
                "GroupNorm \(prefix) channels \(channels) not divisible by \(groupCount)"
            )
        }
        self.groupCount = groupCount
        weight = rawWeight.reshaped(1, 1, 1, channels)
        bias = rawBias.reshaped(1, 1, 1, channels)
    }

    func callAsFunction(_ hidden: MLXArray) -> MLXArray {
        let nhwc = hidden.transposed(0, 2, 3, 1).asType(.float32)
        let batch = nhwc.shape[0]
        let channels = nhwc.shape.last!
        let groupSize = channels / groupCount
        var output = nhwc
            .reshaped(batch, -1, groupCount, groupSize)
            .transposed(0, 2, 1, 3)
            .reshaped(batch, groupCount, -1)
        output = MLXFast.layerNorm(output, weight: nil, bias: nil, eps: eps)
        output = output
            .reshaped(batch, groupCount, -1, groupSize)
            .transposed(0, 2, 1, 3)
            .reshaped(nhwc.shape)
        return (output * weight + bias).asType(hidden.dtype).transposed(0, 3, 1, 2)
    }
}
