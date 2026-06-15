import Foundation
import MLX

struct BonsaiSafetensors {
    private let tensors: [String: MLXArray]

    init(directoryURL: URL) throws {
        let urls = try Self.safetensorURLs(in: directoryURL)
        guard !urls.isEmpty else {
            throw BonsaiRuntimeError.invalidModelLayout("no .safetensors files in \(directoryURL.lastPathComponent)")
        }

        var merged = [String: MLXArray]()
        for url in urls {
            let arrays = try loadArrays(url: url)
            for (key, value) in arrays {
                merged[key] = value
            }
        }
        tensors = merged
    }

    subscript(_ key: String) -> MLXArray? {
        tensors[key]
    }

    func require(_ key: String, dtype: DType = .bfloat16) throws -> MLXArray {
        guard let tensor = tensors[key] else {
            throw BonsaiRuntimeError.missingTensor(key)
        }
        return tensor.dtype == dtype ? tensor : tensor.asType(dtype)
    }

    func optional(_ key: String, dtype: DType = .bfloat16) -> MLXArray? {
        guard let tensor = tensors[key] else {
            return nil
        }
        return tensor.dtype == dtype ? tensor : tensor.asType(dtype)
    }

    func requirePackedLinear(
        weightKey: String,
        bits: Int,
        groupSize: Int,
        dtype: DType = .bfloat16
    ) throws -> BonsaiLinearWeight {
        guard weightKey.hasSuffix(".weight") else {
            throw BonsaiRuntimeError.invalidModelLayout("expected tensor key ending in .weight: \(weightKey)")
        }

        let prefix = String(weightKey.dropLast(".weight".count))
        let scalesKey = "\(prefix).scales"
        guard let scales = optional(scalesKey, dtype: dtype) else {
            return .dense(try require(weightKey, dtype: dtype))
        }

        let biasesKey = "\(prefix).biases"
        guard let biases = optional(biasesKey, dtype: dtype) else {
            throw BonsaiRuntimeError.missingTensor(biasesKey)
        }
        guard let packed = tensors[weightKey] else {
            throw BonsaiRuntimeError.missingTensor(weightKey)
        }
        guard packed.dtype == .uint32 else {
            throw BonsaiRuntimeError.invalidModelLayout("packed tensor \(weightKey) must be uint32")
        }
        return .packed(
            BonsaiPackedWeight(
                packed: packed,
                scales: scales,
                biases: biases,
                bits: bits,
                groupSize: groupSize
            )
        )
    }

    func requirePackedEmbedding(
        weightKey: String,
        bits: Int,
        groupSize: Int,
        dtype: DType = .bfloat16
    ) throws -> BonsaiEmbedding {
        guard weightKey.hasSuffix(".weight") else {
            throw BonsaiRuntimeError.invalidModelLayout("expected tensor key ending in .weight: \(weightKey)")
        }

        let prefix = String(weightKey.dropLast(".weight".count))
        let scalesKey = "\(prefix).scales"
        guard let scales = optional(scalesKey, dtype: dtype) else {
            return BonsaiEmbedding(weight: .dense(try require(weightKey, dtype: dtype)))
        }

        let biasesKey = "\(prefix).biases"
        guard let biases = optional(biasesKey, dtype: dtype) else {
            throw BonsaiRuntimeError.missingTensor(biasesKey)
        }
        guard let packed = tensors[weightKey] else {
            throw BonsaiRuntimeError.missingTensor(weightKey)
        }
        guard packed.dtype == .uint32 else {
            throw BonsaiRuntimeError.invalidModelLayout("packed tensor \(weightKey) must be uint32")
        }
        return BonsaiEmbedding(
            weight: .packed(
                BonsaiPackedWeight(
                    packed: packed,
                    scales: scales,
                    biases: biases,
                    bits: bits,
                    groupSize: groupSize
                )
            )
        )
    }

    private static func safetensorURLs(in directoryURL: URL) throws -> [URL] {
        guard let enumerator = FileManager.default.enumerator(
            at: directoryURL,
            includingPropertiesForKeys: [.isRegularFileKey],
            options: [.skipsHiddenFiles]
        ) else {
            return []
        }

        return enumerator
            .compactMap { $0 as? URL }
            .filter { $0.pathExtension == "safetensors" }
            .sorted { $0.lastPathComponent < $1.lastPathComponent }
    }
}

struct BonsaiQuantizationConfig: Decodable {
    let bits: Int
    let groupSize: Int

    enum CodingKeys: String, CodingKey {
        case bits
        case groupSize = "group_size"
    }

    static func load(from packedTransformerURL: URL) throws -> BonsaiQuantizationConfig {
        let url = packedTransformerURL.appendingPathComponent("quantization_config.json", isDirectory: false)
        let data = try Data(contentsOf: url)
        let config = try JSONDecoder().decode(BonsaiQuantizationConfig.self, from: data)
        guard (config.bits == 1 || config.bits == 2), config.groupSize == 128 else {
            throw BonsaiRuntimeError.unsupportedQuantization(
                bits: config.bits,
                groupSize: config.groupSize
            )
        }
        return config
    }
}

struct BonsaiPackedWeight {
    let packed: MLXArray
    let scales: MLXArray
    let biases: MLXArray
    let bits: Int
    let groupSize: Int
}

enum BonsaiLinearWeight {
    case dense(MLXArray)
    case packed(BonsaiPackedWeight)
}

enum BonsaiEmbeddingWeight {
    case dense(MLXArray)
    case packed(BonsaiPackedWeight)
}

struct BonsaiEmbedding {
    let weight: BonsaiEmbeddingWeight

    var evaluationArrays: [MLXArray] {
        switch weight {
        case .dense(let dense):
            return [dense]
        case .packed(let packed):
            return [packed.packed, packed.scales, packed.biases]
        }
    }

    func callAsFunction(_ inputIds: MLXArray) -> MLXArray {
        switch weight {
        case .dense(let dense):
            return dense[inputIds]
        case .packed(let packed):
            let shape = inputIds.shape
            let flatIds = inputIds.flattened()
            let output = dequantized(
                packed.packed[flatIds],
                scales: packed.scales[flatIds],
                biases: packed.biases[flatIds],
                groupSize: packed.groupSize,
                bits: packed.bits,
                dtype: .bfloat16
            )
            return output.reshaped(shape + [-1])
        }
    }
}

struct BonsaiLinear {
    let weight: BonsaiLinearWeight
    let bias: MLXArray?

    init(weight: BonsaiLinearWeight, bias: MLXArray? = nil) {
        self.weight = weight
        self.bias = bias
    }

    init(dense weight: MLXArray, bias: MLXArray? = nil) {
        self.weight = .dense(weight.asType(.bfloat16))
        self.bias = bias?.asType(.bfloat16)
    }

    func callAsFunction(_ x: MLXArray) -> MLXArray {
        let output: MLXArray
        switch weight {
        case .dense(let dense):
            output = matmul(x, dense.T)
        case .packed(let packed):
            output = quantizedMM(
                x.asType(.bfloat16),
                packed.packed,
                scales: packed.scales,
                biases: packed.biases,
                transpose: true,
                groupSize: packed.groupSize,
                bits: packed.bits
            )
        }

        if let bias {
            return output + bias
        }
        return output
    }
}
