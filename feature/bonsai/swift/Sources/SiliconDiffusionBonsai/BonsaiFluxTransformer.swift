import Foundation
import MLX
import MLXNN

final class BonsaiFluxTransformer {
    private let xEmbedder: BonsaiLinear
    private let contextEmbedder: BonsaiLinear
    private let timeGuidanceEmbedder: FluxTimeGuidanceEmbedder
    private let normOutLinear: BonsaiLinear
    private let projOut: BonsaiLinear
    private let doubleBlocks: [FluxDoubleBlock]
    private let singleBlocks: [FluxSingleBlock]
    private let posEmbed = FluxPosEmbed()
    private let dim = 3072
    private let layerNormEps: Float = 1e-6

    init(layout: BonsaiModelLayout) throws {
        let quantization = try BonsaiQuantizationConfig.load(from: layout.packedTransformerURL)
        let tensors = try BonsaiSafetensors(directoryURL: layout.packedTransformerURL)
        xEmbedder = BonsaiLinear(dense: try tensors.require("x_embedder.weight"))
        contextEmbedder = BonsaiLinear(dense: try tensors.require("context_embedder.weight"))
        normOutLinear = BonsaiLinear(dense: try tensors.require("norm_out.linear.weight"))
        projOut = BonsaiLinear(dense: try tensors.require("proj_out.weight"))
        timeGuidanceEmbedder = try FluxTimeGuidanceEmbedder(tensors: tensors)

        let sharedDoubleModImg = BonsaiLinear(dense: try tensors.require("double_stream_modulation_img.linear.weight"))
        let sharedDoubleModTxt = BonsaiLinear(dense: try tensors.require("double_stream_modulation_txt.linear.weight"))
        let sharedSingleMod = BonsaiLinear(dense: try tensors.require("single_stream_modulation.linear.weight"))
        doubleBlocks = try (0..<5).map {
            try FluxDoubleBlock(
                index: $0,
                tensors: tensors,
                quantization: quantization,
                imgModulation: sharedDoubleModImg,
                textModulation: sharedDoubleModTxt
            )
        }
        singleBlocks = try (0..<20).map {
            try FluxSingleBlock(
                index: $0,
                tensors: tensors,
                quantization: quantization,
                modulation: sharedSingleMod
            )
        }
    }

    func callAsFunction(
        hiddenStates latents: MLXArray,
        encoderHiddenStates promptEmbeddings: MLXArray,
        timestep: Float,
        imageIds: MLXArray,
        textIds: MLXArray
    ) -> MLXArray {
        let batch = latents.shape[0]
        let timestepValue = timestep <= 1.0 ? timestep * 1000.0 : timestep
        let timestepArray = MLXArray(Array(repeating: timestepValue, count: batch), [batch])
            .asType(latents.dtype)
        let temb = timeGuidanceEmbedder(timestep: timestepArray).asType(.bfloat16)

        let imageRotary = posEmbed(imageIds.ndim == 3 ? imageIds[0] : imageIds)
        let textRotary = posEmbed(textIds.ndim == 3 ? textIds[0] : textIds)
        let rotaryCos = concatenated([textRotary.cos, imageRotary.cos], axis: 0)
        let rotarySin = concatenated([textRotary.sin, imageRotary.sin], axis: 0)

        var image = xEmbedder(latents)
        var text = contextEmbedder(promptEmbeddings)
        let textSeqLen = text.shape[1]

        for block in doubleBlocks {
            let output = block(image: image, text: text, temb: temb, rotaryCos: rotaryCos, rotarySin: rotarySin)
            text = output.text
            image = output.image
            eval(text, image)
            BonsaiMlxMemory.reclaimCache()
        }

        var hidden = concatenated([text, image], axis: 1)
        for block in singleBlocks {
            hidden = block(hidden: hidden, temb: temb, rotaryCos: rotaryCos, rotarySin: rotarySin)
            eval(hidden)
            BonsaiMlxMemory.reclaimCache()
        }

        let imageOut = hidden[0..., textSeqLen..., 0...]
        let normMod = normOutLinear(silu(temb))
        let scale = normMod[0..., 0..<dim]
        let shift = normMod[0..., dim...]
        let normed = MLXFast.layerNorm(
            imageOut,
            weight: (1.0 + scale).reshaped(-1),
            bias: shift.reshaped(-1),
            eps: layerNormEps
        )
        return projOut(normed)
    }
}

private final class FluxTimeGuidanceEmbedder {
    private let linear1: BonsaiLinear
    private let linear2: BonsaiLinear
    private let inChannels = 256

    init(tensors: BonsaiSafetensors) throws {
        linear1 = BonsaiLinear(dense: try tensors.require("time_guidance_embed.timestep_embedder.linear_1.weight"))
        linear2 = BonsaiLinear(dense: try tensors.require("time_guidance_embed.timestep_embedder.linear_2.weight"))
    }

    func callAsFunction(timestep: MLXArray) -> MLXArray {
        linear2(silu(linear1(Self.embedding(timesteps: timestep.asType(.float32), dim: inChannels))))
    }

    private static func embedding(timesteps: MLXArray, dim: Int) -> MLXArray {
        let half = dim / 2
        let freqs = exp(-log(10000.0) * MLXArray(0..<half).asType(.float32) / Float(half))
        let args = timesteps.expandedDimensions(axis: -1) * freqs.expandedDimensions(axis: 0)
        let values = concatenated([sin(args), cos(args)], axis: -1)
        return concatenated([values[0..., half...], values[0..., 0..<half]], axis: -1)
    }
}

private struct FluxPosEmbed {
    private let theta: Float = 2000.0
    private let axesDims = [32, 32, 32, 32]

    func callAsFunction(_ ids: MLXArray) -> (cos: MLXArray, sin: MLXArray) {
        let positions = ids.asType(.float32)
        var cosParts = [MLXArray]()
        var sinParts = [MLXArray]()
        for axis in 0..<axesDims.count {
            let rope = rope1D(dim: axesDims[axis], positions: positions[0..., axis])
            cosParts.append(rope.cos)
            sinParts.append(rope.sin)
        }
        return (concatenated(cosParts, axis: -1), concatenated(sinParts, axis: -1))
    }

    private func rope1D(dim: Int, positions: MLXArray) -> (cos: MLXArray, sin: MLXArray) {
        let scale = MLXArray(stride(from: 0, to: dim, by: 2).map(Float.init), [dim / 2]).asType(.float32) / Float(dim)
        let omega = 1.0 / pow(theta, scale)
        let out = positions.expandedDimensions(axis: -1) * omega.expandedDimensions(axis: 0)
        return (cos(out), sin(out))
    }
}

private final class FluxSingleBlock {
    private let modulation: BonsaiLinear
    private let qkvMlpProj: BonsaiLinear
    private let outProj: BonsaiLinear
    private let normQ: MLXArray
    private let normK: MLXArray
    private let dim = 3072
    private let heads = 24
    private let headDim = 128
    private let mlpHiddenDim = 9216
    private let layerNormEps: Float = 1e-6
    private let rmsNormEps: Float = 1e-5

    init(
        index: Int,
        tensors: BonsaiSafetensors,
        quantization: BonsaiQuantizationConfig,
        modulation: BonsaiLinear
    ) throws {
        let prefix = "single_transformer_blocks.\(index).attn"
        self.modulation = modulation
        qkvMlpProj = try Self.linear(
            key: "\(prefix).to_qkv_mlp_proj.weight",
            tensors: tensors,
            quantization: quantization
        )
        outProj = try Self.linear(
            key: "\(prefix).to_out.weight",
            tensors: tensors,
            quantization: quantization
        )
        normQ = try tensors.require("\(prefix).norm_q.weight")
        normK = try tensors.require("\(prefix).norm_k.weight")
    }

    func callAsFunction(hidden: MLXArray, temb: MLXArray, rotaryCos: MLXArray, rotarySin: MLXArray) -> MLXArray {
        let mod = modulation(silu(temb)).reshaped(temb.shape[0], 1, 3, dim)
        let shift = mod[0..., 0..., 0, 0...]
        let scale = mod[0..., 0..., 1, 0...]
        let gate = mod[0..., 0..., 2, 0...]
        let normed = MLXFast.layerNorm(
            hidden,
            weight: (1.0 + scale).reshaped(-1),
            bias: shift.reshaped(-1),
            eps: layerNormEps
        )
        let fused = qkvMlpProj(normed)
        let queryRaw = fused[0..., 0..., 0..<dim]
        let keyRaw = fused[0..., 0..., dim..<(2 * dim)]
        let valueRaw = fused[0..., 0..., (2 * dim)..<(3 * dim)]
        let mlpHidden = fused[0..., 0..., (3 * dim)...]

        let batch = hidden.shape[0]
        let seqLen = hidden.shape[1]
        var query = queryRaw.reshaped(batch, seqLen, heads, headDim).transposed(0, 2, 1, 3)
        var key = keyRaw.reshaped(batch, seqLen, heads, headDim).transposed(0, 2, 1, 3)
        let value = valueRaw.reshaped(batch, seqLen, heads, headDim).transposed(0, 2, 1, 3)

        query = FluxRope.applyRmsAndRope(query, normWeight: normQ, cosValues: rotaryCos, sinValues: rotarySin, eps: rmsNormEps)
        key = FluxRope.applyRmsAndRope(key, normWeight: normK, cosValues: rotaryCos, sinValues: rotarySin, eps: rmsNormEps)

        let attended = MLXFast.scaledDotProductAttention(
            queries: query,
            keys: key,
            values: value,
            scale: 1.0 / Float(sqrt(Double(headDim))),
            mask: nil
        )
            .transposed(0, 2, 1, 3)
            .reshaped(batch, seqLen, dim)
        let mlpOutput = FluxActivation.swiglu(mlpHidden)
        let blockOutput = outProj(concatenated([attended, mlpOutput], axis: -1))
        return hidden + gate * blockOutput
    }

    fileprivate static func linear(
        key: String,
        tensors: BonsaiSafetensors,
        quantization: BonsaiQuantizationConfig
    ) throws -> BonsaiLinear {
        BonsaiLinear(
            weight: try tensors.requirePackedLinear(
                weightKey: key,
                bits: quantization.bits,
                groupSize: quantization.groupSize
            )
        )
    }
}

private final class FluxDoubleBlock {
    private let imgModulation: BonsaiLinear
    private let textModulation: BonsaiLinear
    private let toQ: BonsaiLinear
    private let toK: BonsaiLinear
    private let toV: BonsaiLinear
    private let addQ: BonsaiLinear
    private let addK: BonsaiLinear
    private let addV: BonsaiLinear
    private let toOut: BonsaiLinear
    private let toAddOut: BonsaiLinear
    private let ffIn: BonsaiLinear
    private let ffOut: BonsaiLinear
    private let ffContextIn: BonsaiLinear
    private let ffContextOut: BonsaiLinear
    private let normQ: MLXArray
    private let normK: MLXArray
    private let normAddedQ: MLXArray
    private let normAddedK: MLXArray
    private let dim = 3072
    private let heads = 24
    private let headDim = 128
    private let layerNormEps: Float = 1e-6
    private let rmsNormEps: Float = 1e-5

    init(
        index: Int,
        tensors: BonsaiSafetensors,
        quantization: BonsaiQuantizationConfig,
        imgModulation: BonsaiLinear,
        textModulation: BonsaiLinear
    ) throws {
        let block = "transformer_blocks.\(index)"
        let attn = "\(block).attn"
        self.imgModulation = imgModulation
        self.textModulation = textModulation
        toQ = try FluxSingleBlock.linear(key: "\(attn).to_q.weight", tensors: tensors, quantization: quantization)
        toK = try FluxSingleBlock.linear(key: "\(attn).to_k.weight", tensors: tensors, quantization: quantization)
        toV = try FluxSingleBlock.linear(key: "\(attn).to_v.weight", tensors: tensors, quantization: quantization)
        addQ = try FluxSingleBlock.linear(key: "\(attn).add_q_proj.weight", tensors: tensors, quantization: quantization)
        addK = try FluxSingleBlock.linear(key: "\(attn).add_k_proj.weight", tensors: tensors, quantization: quantization)
        addV = try FluxSingleBlock.linear(key: "\(attn).add_v_proj.weight", tensors: tensors, quantization: quantization)
        toOut = try FluxSingleBlock.linear(key: "\(attn).to_out.0.weight", tensors: tensors, quantization: quantization)
        toAddOut = try FluxSingleBlock.linear(key: "\(attn).to_add_out.weight", tensors: tensors, quantization: quantization)
        ffIn = try FluxSingleBlock.linear(key: "\(block).ff.linear_in.weight", tensors: tensors, quantization: quantization)
        ffOut = try FluxSingleBlock.linear(key: "\(block).ff.linear_out.weight", tensors: tensors, quantization: quantization)
        ffContextIn = try FluxSingleBlock.linear(key: "\(block).ff_context.linear_in.weight", tensors: tensors, quantization: quantization)
        ffContextOut = try FluxSingleBlock.linear(key: "\(block).ff_context.linear_out.weight", tensors: tensors, quantization: quantization)
        normQ = try tensors.require("\(attn).norm_q.weight")
        normK = try tensors.require("\(attn).norm_k.weight")
        normAddedQ = try tensors.require("\(attn).norm_added_q.weight")
        normAddedK = try tensors.require("\(attn).norm_added_k.weight")
    }

    func callAsFunction(
        image: MLXArray,
        text: MLXArray,
        temb: MLXArray,
        rotaryCos: MLXArray,
        rotarySin: MLXArray
    ) -> (text: MLXArray, image: MLXArray) {
        let imgMod = modulation(imgModulation, temb: temb)
        let txtMod = modulation(textModulation, temb: temb)

        let normImage = MLXFast.layerNorm(image, weight: imgMod.msaWeight, bias: imgMod.msaBias, eps: layerNormEps)
        let normText = MLXFast.layerNorm(text, weight: txtMod.msaWeight, bias: txtMod.msaBias, eps: layerNormEps)

        let batch = image.shape[0]
        let imageSeq = image.shape[1]
        let textSeq = text.shape[1]

        var imageQ = toQ(normImage).reshaped(batch, imageSeq, heads, headDim).transposed(0, 2, 1, 3)
        var imageK = toK(normImage).reshaped(batch, imageSeq, heads, headDim).transposed(0, 2, 1, 3)
        let imageV = toV(normImage).reshaped(batch, imageSeq, heads, headDim).transposed(0, 2, 1, 3)
        var textQ = addQ(normText).reshaped(batch, textSeq, heads, headDim).transposed(0, 2, 1, 3)
        var textK = addK(normText).reshaped(batch, textSeq, heads, headDim).transposed(0, 2, 1, 3)
        let textV = addV(normText).reshaped(batch, textSeq, heads, headDim).transposed(0, 2, 1, 3)

        let textCos = rotaryCos[0..<textSeq, 0...]
        let textSin = rotarySin[0..<textSeq, 0...]
        let imageCos = rotaryCos[textSeq..., 0...]
        let imageSin = rotarySin[textSeq..., 0...]
        textQ = FluxRope.applyRmsAndRope(textQ, normWeight: normAddedQ, cosValues: textCos, sinValues: textSin, eps: rmsNormEps)
        textK = FluxRope.applyRmsAndRope(textK, normWeight: normAddedK, cosValues: textCos, sinValues: textSin, eps: rmsNormEps)
        imageQ = FluxRope.applyRmsAndRope(imageQ, normWeight: normQ, cosValues: imageCos, sinValues: imageSin, eps: rmsNormEps)
        imageK = FluxRope.applyRmsAndRope(imageK, normWeight: normK, cosValues: imageCos, sinValues: imageSin, eps: rmsNormEps)

        let fullQ = concatenated([textQ, imageQ], axis: 2)
        let fullK = concatenated([textK, imageK], axis: 2)
        let fullV = concatenated([textV, imageV], axis: 2)
        let attention = MLXFast.scaledDotProductAttention(
            queries: fullQ,
            keys: fullK,
            values: fullV,
            scale: 1.0 / Float(sqrt(Double(headDim))),
            mask: nil
        )
            .transposed(0, 2, 1, 3)
            .reshaped(batch, textSeq + imageSeq, dim)
        let textAttention = toAddOut(attention[0..., 0..<textSeq, 0...])
        let imageAttention = toOut(attention[0..., textSeq..., 0...])

        var imageOut = image + imgMod.msaGate * imageAttention
        let normImageMlp = MLXFast.layerNorm(imageOut, weight: imgMod.mlpWeight, bias: imgMod.mlpBias, eps: layerNormEps)
        imageOut = imageOut + imgMod.mlpGate * ffOut(FluxActivation.swiglu(ffIn(normImageMlp)))

        var textOut = text + txtMod.msaGate * textAttention
        let normTextMlp = MLXFast.layerNorm(textOut, weight: txtMod.mlpWeight, bias: txtMod.mlpBias, eps: layerNormEps)
        textOut = textOut + txtMod.mlpGate * ffContextOut(FluxActivation.swiglu(ffContextIn(normTextMlp)))
        return (textOut, imageOut)
    }

    private func modulation(_ linear: BonsaiLinear, temb: MLXArray) -> FluxDoubleModulation {
        let mod = linear(silu(temb)).reshaped(temb.shape[0], 1, 2, 3, dim)
        let shiftMsa = mod[0..., 0..., 0, 0, 0...]
        let scaleMsa = mod[0..., 0..., 0, 1, 0...]
        let gateMsa = mod[0..., 0..., 0, 2, 0...]
        let shiftMlp = mod[0..., 0..., 1, 0, 0...]
        let scaleMlp = mod[0..., 0..., 1, 1, 0...]
        let gateMlp = mod[0..., 0..., 1, 2, 0...]
        return FluxDoubleModulation(
            msaWeight: (1.0 + scaleMsa).reshaped(-1),
            msaBias: shiftMsa.reshaped(-1),
            msaGate: gateMsa,
            mlpWeight: (1.0 + scaleMlp).reshaped(-1),
            mlpBias: shiftMlp.reshaped(-1),
            mlpGate: gateMlp
        )
    }
}

private struct FluxDoubleModulation {
    let msaWeight: MLXArray
    let msaBias: MLXArray
    let msaGate: MLXArray
    let mlpWeight: MLXArray
    let mlpBias: MLXArray
    let mlpGate: MLXArray
}

private enum FluxActivation {
    static func swiglu(_ x: MLXArray) -> MLXArray {
        let half = x.shape.last! / 2
        return silu(x[0..., 0..., 0..<half]) * x[0..., 0..., half...]
    }
}

private enum FluxRope {
    static func applyRmsAndRope(
        _ x: MLXArray,
        normWeight: MLXArray,
        cosValues: MLXArray,
        sinValues: MLXArray,
        eps: Float
    ) -> MLXArray {
        let outputDType = x.dtype
        let x = MLXFast
            .rmsNorm(x.asType(.float32), weight: normWeight.asType(.float32), eps: eps)
            .asType(.float32)
        let shape = x.shape
        let paired = x.reshaped(shape.dropLast() + [shape.last! / 2, 2])
        let real = paired[0..., 0..., 0..., 0..., 0]
        let imag = paired[0..., 0..., 0..., 0..., 1]
        let cosValues = cosValues
            .reshaped(1, 1, cosValues.shape[0], cosValues.shape[1])
            .asType(.float32)
        let sinValues = sinValues
            .reshaped(1, 1, sinValues.shape[0], sinValues.shape[1])
            .asType(.float32)
        let rotatedReal = real * cosValues + (-imag) * sinValues
        let rotatedImag = imag * cosValues + real * sinValues
        return stacked([rotatedReal, rotatedImag], axis: -1)
            .reshaped(shape)
            .asType(outputDType)
    }
}
