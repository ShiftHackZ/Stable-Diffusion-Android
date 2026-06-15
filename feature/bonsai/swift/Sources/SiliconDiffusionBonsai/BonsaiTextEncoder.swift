import Foundation
import Hub
import MLX
import MLXNN
import Tokenizers

struct BonsaiPromptEmbeddings {
    let embeddings: MLXArray
    let textIds: MLXArray
}

final class BonsaiTextEncoder {
    private let tokenizer: Tokenizer
    private let model: Qwen3TextEncoderModel
    private let maxSequenceLength: Int
    private let padTokenId: Int

    init(layout: BonsaiModelLayout, maxSequenceLength: Int = 512) throws {
        tokenizer = try Self.loadTokenizer(from: layout.tokenizerURL)
        model = try Qwen3TextEncoderModel(directoryURL: layout.textEncoderURL)
        self.maxSequenceLength = maxSequenceLength
        padTokenId = Self.loadPadTokenId(from: layout.tokenizerURL, tokenizer: tokenizer)
    }

    func encode(prompt: String) throws -> BonsaiPromptEmbeddings {
        let encoded = tokenizer.encode(text: Self.chatFormattedPrompt(prompt))
        let effectiveMaxSequenceLength = bucketedSequenceLength(tokenCount: encoded.count)
        let ids = Array(encoded.prefix(effectiveMaxSequenceLength))
        let padded = ids + Array(repeating: padTokenId, count: max(0, effectiveMaxSequenceLength - ids.count))
        let mask = Array(repeating: Int32(1), count: ids.count)
            + Array(repeating: Int32(0), count: max(0, effectiveMaxSequenceLength - ids.count))

        print(
            "[Bonsai] text encode tokenCount=\(encoded.count) effectiveLength=\(effectiveMaxSequenceLength) " +
                "padId=\(padTokenId) eosId=\(tokenizer.eosTokenId?.description ?? "nil") firstIds=\(Array(ids.prefix(8)))"
        )
        let inputIds = MLXArray(padded.map(Int32.init), [1, effectiveMaxSequenceLength])
        let attentionMask = MLXArray(mask, [1, effectiveMaxSequenceLength])
        let embeds = model.promptEmbeddings(inputIds: inputIds, attentionMask: attentionMask)
        return BonsaiPromptEmbeddings(
            embeddings: embeds,
            textIds: Self.textIds(length: effectiveMaxSequenceLength)
        )
    }

    private func bucketedSequenceLength(tokenCount: Int) -> Int {
        max(1, maxSequenceLength)
    }

    static func chatFormattedPrompt(_ prompt: String) -> String {
        "<|im_start|>user\n\(prompt)<|im_end|>\n<|im_start|>assistant\n<think>\n\n</think>\n\n"
    }

    private static func loadTokenizer(from url: URL) throws -> Tokenizer {
        var tokenizerConfigDictionary = try Self.configDictionary(
            url.appendingPathComponent("tokenizer_config.json", isDirectory: false)
        )
        if let tokenizerClass = tokenizerConfigDictionary["tokenizer_class"] as? String,
           tokenizerClass.hasPrefix("Qwen") {
            tokenizerConfigDictionary["tokenizer_class"] = "GPT2Tokenizer"
        }
        let tokenizerConfig = Config(tokenizerConfigDictionary)
        var tokenizerDataDictionary = try Self.configDictionary(
            url.appendingPathComponent("tokenizer.json", isDirectory: false)
        )
        if var model = tokenizerDataDictionary["model"] as? [String: Any],
           let mergePairs = model["merges"] as? [[Any]] {
            model["merges"] = mergePairs.compactMap { pair -> String? in
                guard pair.count == 2,
                      let first = pair[0] as? String,
                      let second = pair[1] as? String
                else {
                    return nil
                }
                return "\(first) \(second)"
            }
            tokenizerDataDictionary["model"] = model
        }
        let tokenizerData = Config(tokenizerDataDictionary)
        return try AutoTokenizer.from(tokenizerConfig: tokenizerConfig, tokenizerData: tokenizerData)
    }

    private static func loadPadTokenId(from url: URL, tokenizer: Tokenizer) -> Int {
        if let token = try? tokenString(named: "pad_token", from: url.appendingPathComponent("tokenizer_config.json")),
           let id = tokenizer.convertTokenToId(token) {
            return id
        }
        if let token = try? tokenString(named: "pad_token", from: url.appendingPathComponent("special_tokens_map.json")),
           let id = tokenizer.convertTokenToId(token) {
            return id
        }
        return tokenizer.eosTokenId ?? 0
    }

    private static func tokenString(named key: String, from url: URL) throws -> String? {
        let dictionary = try configDictionary(url)
        if let token = dictionary[key] as? String {
            return token
        }
        if let token = dictionary[key] as? [String: Any] {
            return token["content"] as? String
        }
        return nil
    }

    private static func config(_ url: URL) throws -> Config {
        Config(try configDictionary(url))
    }

    private static func configDictionary(_ url: URL) throws -> [String: Any] {
        let data = try Data(contentsOf: url)
        guard let dictionary = try JSONSerialization.jsonObject(with: data) as? [String: Any] else {
            throw BonsaiRuntimeError.invalidModelLayout("invalid tokenizer JSON at \(url.lastPathComponent)")
        }
        return dictionary
    }

    private static func textIds(length: Int) -> MLXArray {
        var values = [Int32]()
        values.reserveCapacity(length * 4)
        for index in 0..<length {
            values.append(0)
            values.append(0)
            values.append(0)
            values.append(Int32(index))
        }
        return MLXArray(values, [1, length, 4])
    }
}

private final class Qwen3TextEncoderModel {
    private let embedding: BonsaiEmbedding
    private let layers: [Qwen3TextLayer]
    private let normWeight: MLXArray
    private let rotaryEmbedding: Qwen3TextRotaryEmbedding
    private let hiddenStateLayers = [9, 18, 27]
    private let hiddenSize = 2560

    init(directoryURL: URL) throws {
        let tensors = try BonsaiSafetensors(directoryURL: directoryURL)
        embedding = try tensors.requirePackedEmbedding(
            weightKey: Self.key("embed_tokens.weight", in: tensors),
            bits: 4,
            groupSize: 64
        )
        normWeight = try tensors.require(Self.key("norm.weight", in: tensors))
        rotaryEmbedding = Qwen3TextRotaryEmbedding(headDimension: 128)
        layers = try (0..<36).map { index in
            try Qwen3TextLayer(index: index, tensors: tensors)
        }
        eval(embedding.evaluationArrays + [normWeight])
    }

    func promptEmbeddings(inputIds: MLXArray, attentionMask: MLXArray) -> MLXArray {
        var hiddenStates = embedding(inputIds)
        var selectedStates = [MLXArray]()
        let mask = Self.attentionMask(hiddenStates: hiddenStates, attentionMask: attentionMask)
        let positionIds = MLXArray((0..<inputIds.shape[1]).map(Int32.init), [1, inputIds.shape[1]])
        let positionEmbeddings = rotaryEmbedding(hiddenStates: hiddenStates, positionIds: positionIds)

        for (index, layer) in layers.enumerated() {
            hiddenStates = layer(
                hiddenStates: hiddenStates,
                attentionMask: mask,
                positionEmbeddings: positionEmbeddings
            )
            eval(hiddenStates)
            let stateIndex = index + 1
            if hiddenStateLayers.contains(stateIndex) {
                selectedStates.append(hiddenStates)
            } else {
                BonsaiMlxMemory.reclaimCache()
            }
        }

        let stackedStates = stacked(selectedStates, axis: 1)
        let shape = stackedStates.shape
        return stackedStates
            .transposed(0, 2, 1, 3)
            .reshaped(shape[0], shape[2], shape[1] * hiddenSize)
            .asType(.bfloat16)
    }

    fileprivate static func key(_ suffix: String, in tensors: BonsaiSafetensors) -> String {
        if tensors[suffix] != nil {
            return suffix
        }
        return "model.\(suffix)"
    }

    private static func attentionMask(hiddenStates: MLXArray, attentionMask: MLXArray) -> MLXArray {
        let batch = hiddenStates.shape[0]
        let seqLen = hiddenStates.shape[1]
        let negativePadding = MLXArray(
            Array(repeating: -Float.infinity, count: attentionMask.shape.reduce(1, *)),
            attentionMask.shape
        ).asType(hiddenStates.dtype)
        let padding = `where`(
            attentionMask .== MLXArray(Int32(1)),
            MLXArray.zeros(attentionMask.shape, dtype: hiddenStates.dtype),
            negativePadding
        )
            .expandedDimensions(axis: 1)
            .expandedDimensions(axis: 1)

        var causalValues = [Float]()
        causalValues.reserveCapacity(seqLen * seqLen)
        for row in 0..<seqLen {
            for column in 0..<seqLen {
                causalValues.append(column > row ? -Float.infinity : 0.0)
            }
        }
        let causal = MLXArray(causalValues, [1, 1, seqLen, seqLen])
            .asType(hiddenStates.dtype)
        let causalBroadcast = broadcast(causal, to: [batch, 1, seqLen, seqLen])
        return causalBroadcast + padding
    }
}

private struct Qwen3TextRotaryEmbedding {
    private let invFreq: MLXArray

    init(headDimension: Int, base: Float = 1_000_000.0) {
        let values = stride(from: 0, to: headDimension, by: 2).map { index in
            1.0 / pow(base, Float(index) / Float(headDimension))
        }
        invFreq = MLXArray(values, [values.count]).asType(.float32)
    }

    func callAsFunction(hiddenStates: MLXArray, positionIds: MLXArray) -> (cos: MLXArray, sin: MLXArray) {
        let pos = positionIds.asType(.float32).expandedDimensions(axis: -1)
        let freqs = pos * invFreq.expandedDimensions(axis: 0).expandedDimensions(axis: 0)
        let emb = concatenated([freqs, freqs], axis: -1)
        return (cos(emb).asType(hiddenStates.dtype), sin(emb).asType(hiddenStates.dtype))
    }
}

private final class Qwen3TextLayer {
    private let inputNorm: MLXArray
    private let postAttentionNorm: MLXArray
    private let attention: Qwen3TextAttention
    private let mlp: Qwen3TextMLP
    private let rmsEps: Float = 1e-6

    init(index: Int, tensors: BonsaiSafetensors) throws {
        let prefix = Self.prefix(index)
        inputNorm = try tensors.require(Qwen3TextEncoderModel.key("\(prefix).input_layernorm.weight", in: tensors))
        postAttentionNorm = try tensors.require(Qwen3TextEncoderModel.key("\(prefix).post_attention_layernorm.weight", in: tensors))
        attention = try Qwen3TextAttention(prefix: "\(prefix).self_attn", tensors: tensors)
        mlp = try Qwen3TextMLP(prefix: "\(prefix).mlp", tensors: tensors)
    }

    func callAsFunction(
        hiddenStates: MLXArray,
        attentionMask: MLXArray,
        positionEmbeddings: (cos: MLXArray, sin: MLXArray)
    ) -> MLXArray {
        let residual = hiddenStates
        let attended = attention(
            hiddenStates: QwenRMSNorm.apply(hiddenStates, weight: inputNorm, eps: rmsEps),
            attentionMask: attentionMask,
            positionEmbeddings: positionEmbeddings
        )
        let afterAttention = residual + attended
        let afterMlp = mlp(QwenRMSNorm.apply(afterAttention, weight: postAttentionNorm, eps: rmsEps))
        return afterAttention + afterMlp
    }

    private static func prefix(_ index: Int) -> String {
        "layers.\(index)"
    }
}

private final class Qwen3TextAttention {
    private let qProj: BonsaiLinear
    private let kProj: BonsaiLinear
    private let vProj: BonsaiLinear
    private let oProj: BonsaiLinear
    private let qNorm: MLXArray
    private let kNorm: MLXArray
    private let numAttentionHeads = 32
    private let numKeyValueHeads = 8
    private let headDim = 128
    private let scale: Float = 1.0 / Float(sqrt(128.0))
    private let rmsEps: Float = 1e-6

    init(prefix: String, tensors: BonsaiSafetensors) throws {
        qProj = try Self.linear(prefix: prefix, name: "q_proj", tensors: tensors)
        kProj = try Self.linear(prefix: prefix, name: "k_proj", tensors: tensors)
        vProj = try Self.linear(prefix: prefix, name: "v_proj", tensors: tensors)
        oProj = try Self.linear(prefix: prefix, name: "o_proj", tensors: tensors)
        qNorm = try tensors.require(Qwen3TextEncoderModel.key("\(prefix).q_norm.weight", in: tensors))
        kNorm = try tensors.require(Qwen3TextEncoderModel.key("\(prefix).k_norm.weight", in: tensors))
    }

    func callAsFunction(
        hiddenStates: MLXArray,
        attentionMask: MLXArray,
        positionEmbeddings: (cos: MLXArray, sin: MLXArray)
    ) -> MLXArray {
        let batch = hiddenStates.shape[0]
        let length = hiddenStates.shape[1]

        var query = qProj(hiddenStates).reshaped(batch, length, numAttentionHeads, headDim)
        var key = kProj(hiddenStates).reshaped(batch, length, numKeyValueHeads, headDim)
        var value = vProj(hiddenStates).reshaped(batch, length, numKeyValueHeads, headDim)

        query = QwenRMSNorm.apply(query, weight: qNorm, eps: rmsEps).transposed(0, 2, 1, 3)
        key = QwenRMSNorm.apply(key, weight: kNorm, eps: rmsEps).transposed(0, 2, 1, 3)
        value = value.transposed(0, 2, 1, 3)

        let rotated = Self.applyRotary(query: query, key: key, cosValues: positionEmbeddings.cos, sinValues: positionEmbeddings.sin)
        query = rotated.query
        key = Self.repeatKV(rotated.key, groups: numAttentionHeads / numKeyValueHeads)
        value = Self.repeatKV(value, groups: numAttentionHeads / numKeyValueHeads)

        let attended = MLXFast
            .scaledDotProductAttention(
                queries: query.asType(.float32),
                keys: key.asType(.float32),
                values: value.asType(.float32),
                scale: scale,
                mask: attentionMask
            )
            .asType(hiddenStates.dtype)
            .transposed(0, 2, 1, 3)
            .reshaped(batch, length, numAttentionHeads * headDim)
        return oProj(attended)
    }

    fileprivate static func linear(prefix: String, name: String, tensors: BonsaiSafetensors) throws -> BonsaiLinear {
        let weightKey = Qwen3TextEncoderModel.key("\(prefix).\(name).weight", in: tensors)
        let biasKey = Qwen3TextEncoderModel.key("\(prefix).\(name).bias", in: tensors)
        let weight: BonsaiLinearWeight
        if tensors["\(String(weightKey.dropLast(".weight".count))).scales"] != nil {
            weight = try tensors.requirePackedLinear(weightKey: weightKey, bits: 4, groupSize: 64)
        } else {
            weight = .dense(try tensors.require(weightKey))
        }
        return BonsaiLinear(weight: weight, bias: tensors.optional(biasKey))
    }

    private static func applyRotary(
        query: MLXArray,
        key: MLXArray,
        cosValues: MLXArray,
        sinValues: MLXArray
    ) -> (query: MLXArray, key: MLXArray) {
        let cosValues = cosValues.expandedDimensions(axis: 1)
        let sinValues = sinValues.expandedDimensions(axis: 1)
        return (
            query * cosValues + rotateHalf(query) * sinValues,
            key * cosValues + rotateHalf(key) * sinValues
        )
    }

    private static func repeatKV(_ x: MLXArray, groups: Int) -> MLXArray {
        guard groups > 1 else {
            return x
        }
        let expanded = x.expandedDimensions(axis: 2)
        let tiled = repeated(expanded, count: groups, axis: 2)
        return tiled.reshaped(x.shape[0], x.shape[1] * groups, x.shape[2], x.shape[3])
    }

    private static func rotateHalf(_ x: MLXArray) -> MLXArray {
        let half = x.shape.last! / 2
        let first = x[0..., 0..., 0..., 0..<half]
        let second = x[0..., 0..., 0..., half...]
        return concatenated([-second, first], axis: -1)
    }
}

private enum QwenRMSNorm {
    static func apply(_ x: MLXArray, weight: MLXArray, eps: Float) -> MLXArray {
        MLXFast.rmsNorm(
            x.asType(.float32),
            weight: weight.asType(.float32),
            eps: eps
        )
        .asType(x.dtype)
    }
}

private final class Qwen3TextMLP {
    private let gate: BonsaiLinear
    private let up: BonsaiLinear
    private let down: BonsaiLinear

    init(prefix: String, tensors: BonsaiSafetensors) throws {
        gate = try Qwen3TextAttention.linear(prefix: prefix, name: "gate_proj", tensors: tensors)
        up = try Qwen3TextAttention.linear(prefix: prefix, name: "up_proj", tensors: tensors)
        down = try Qwen3TextAttention.linear(prefix: prefix, name: "down_proj", tensors: tensors)
    }

    func callAsFunction(_ hiddenStates: MLXArray) -> MLXArray {
        down(silu(gate(hiddenStates)) * up(hiddenStates))
    }
}
