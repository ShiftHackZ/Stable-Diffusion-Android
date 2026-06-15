import Foundation
import SiliconDiffusionBonsai

private enum ProbeError: Error, CustomStringConvertible {
    case missingModelPath
    case generationFailed(String)
    case emptyImage

    var description: String {
        switch self {
        case .missingModelPath:
            return "Usage: BonsaiRuntimeProbe <model-path> [output-jpeg-path] [width] [height] [steps] [cfg-scale] [prompt] [seed]"
        case .generationFailed(let message):
            return message
        case .emptyImage:
            return "Bonsai runtime returned an empty image payload."
        }
    }
}

private final class ProbeResultBox: @unchecked Sendable {
    private let lock = NSLock()
    private var storage: Result<String, Error>?

    func set(_ result: Result<String, Error>) {
        lock.lock()
        storage = result
        lock.unlock()
    }

    func get() -> Result<String, Error>? {
        lock.lock()
        defer { lock.unlock() }
        return storage
    }
}

@available(iOS 17.0, macOS 14.0, *)
private func runProbe() throws {
    let arguments = Array(CommandLine.arguments.dropFirst())
    guard let modelPath = arguments.first else {
        throw ProbeError.missingModelPath
    }

    let outputPath = arguments.dropFirst().first
    let width = arguments.dropFirst(2).first.flatMap(Int.init) ?? 128
    let height = arguments.dropFirst(3).first.flatMap(Int.init) ?? width
    let steps = arguments.dropFirst(4).first.flatMap(Int32.init) ?? 4
    let cfgScale = arguments.dropFirst(5).first.flatMap(Float.init) ?? 1.0
    let prompt = arguments.dropFirst(6).first ?? "A tiny bonsai tree in a ceramic pot, soft morning light"
    let seed = arguments.dropFirst(7).first ?? "42"
    let generator = SiliconDiffusionBonsaiGenerator()
    let semaphore = DispatchSemaphore(value: 0)
    let startedAt = Date()
    let resultBox = ProbeResultBox()

    generator.generate(
        request: SiliconDiffusionBonsaiGenerator.Request(
            modelPath: modelPath,
            prompt: prompt,
            negativePrompt: "",
            samplingSteps: steps,
            cfgScale: cfgScale,
            width: Int32(width),
            height: Int32(height),
            seed: seed,
            allowNsfw: false
        ),
        onProgress: { progress in
            print("progress \(progress.current)/\(progress.total)")
        },
        completion: { response in
            if let imageBase64 = response.imageBase64 {
                resultBox.set(.success(imageBase64))
            } else {
                resultBox.set(
                    .failure(
                        ProbeError.generationFailed(response.errorMessage ?? "Bonsai generation failed.")
                    )
                )
            }
            semaphore.signal()
        }
    )

    semaphore.wait()
    let imageBase64 = try resultBox.get()?.get() ?? {
        throw ProbeError.generationFailed("Bonsai generation did not return a result.")
    }()
    guard !imageBase64.isEmpty else {
        throw ProbeError.emptyImage
    }

    let byteCount = Data(base64Encoded: imageBase64)?.count ?? 0
    if let outputPath, let data = Data(base64Encoded: imageBase64) {
        try data.write(to: URL(fileURLWithPath: outputPath))
    }
    let elapsed = Date().timeIntervalSince(startedAt)
    print("generated_bytes \(byteCount)")
    print(String(format: "elapsed_seconds %.2f", elapsed))
}

if #available(iOS 17.0, macOS 14.0, *) {
    do {
        try runProbe()
    } catch {
        fputs("\(error)\n", stderr)
        exit(1)
    }
} else {
    fputs("Bonsai runtime requires iOS 17.0 or macOS 14.0.\n", stderr)
    exit(1)
}
