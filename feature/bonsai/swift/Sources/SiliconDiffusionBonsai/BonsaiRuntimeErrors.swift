import Foundation

enum BonsaiRuntimeError: LocalizedError {
    case emptyPrompt
    case invalidSize
    case interrupted
    case modelResourcesNotFound(String)
    case invalidModelLayout(String)
    case invalidModelArchive
    case invalidSeed(String)
    case missingTensor(String)
    case unsupportedQuantization(bits: Int, groupSize: Int)
    case imageEncodingFailed
    case invalidOutputImage(String)
    case runtimeUnavailable(String)

    var errorDescription: String? {
        switch self {
        case .emptyPrompt:
            return "Prompt is required."
        case .invalidSize:
            return "Bonsai image size must be positive and divisible by 32."
        case .interrupted:
            return "Bonsai Image generation interrupted."
        case .modelResourcesNotFound(let path):
            return "Bonsai model resources were not found at \(path)."
        case .invalidModelLayout(let reason):
            return "Invalid Bonsai model layout: \(reason)"
        case .invalidModelArchive:
            return "Bonsai model archive could not be extracted."
        case .invalidSeed(let seed):
            return "Bonsai seed is not a valid integer: \(seed)"
        case .missingTensor(let key):
            return "Bonsai checkpoint is missing tensor: \(key)"
        case .unsupportedQuantization(let bits, let groupSize):
            return "Unsupported Bonsai quantization: \(bits)-bit group \(groupSize)."
        case .imageEncodingFailed:
            return "Bonsai output image could not be encoded."
        case .invalidOutputImage(let reason):
            return "Bonsai output image is invalid: \(reason)"
        case .runtimeUnavailable(let reason):
            return "Bonsai MLX runtime unavailable: \(reason)"
        }
    }
}
