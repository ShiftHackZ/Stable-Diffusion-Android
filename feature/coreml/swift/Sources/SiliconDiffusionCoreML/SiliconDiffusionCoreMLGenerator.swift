import CoreGraphics
import CoreML
import Foundation
import StableDiffusion
import UIKit
import ZIPFoundation

@available(iOS 16.2, *)
public final class SiliconDiffusionCoreMLGenerator {
    public struct Request {
        public let modelPath: String
        public let prompt: String
        public let negativePrompt: String
        public let samplingSteps: Int32
        public let cfgScale: Float
        public let width: Int32
        public let height: Int32
        public let seed: String
        public let allowNsfw: Bool
        public let startingImageBase64: String?
        public let strength: Float

        public init(
            modelPath: String,
            prompt: String,
            negativePrompt: String,
            samplingSteps: Int32,
            cfgScale: Float,
            width: Int32,
            height: Int32,
            seed: String,
            allowNsfw: Bool,
            startingImageBase64: String?,
            strength: Float
        ) {
            self.modelPath = modelPath
            self.prompt = prompt
            self.negativePrompt = negativePrompt
            self.samplingSteps = samplingSteps
            self.cfgScale = cfgScale
            self.width = width
            self.height = height
            self.seed = seed
            self.allowNsfw = allowNsfw
            self.startingImageBase64 = startingImageBase64
            self.strength = strength
        }
    }

    public struct Progress {
        public let current: Int32
        public let total: Int32

        public init(current: Int32, total: Int32) {
            self.current = current
            self.total = total
        }
    }

    public struct Response {
        public let imageBase64: String?
        public let errorMessage: String?

        public init(imageBase64: String?, errorMessage: String?) {
            self.imageBase64 = imageBase64
            self.errorMessage = errorMessage
        }
    }

    private let queue = DispatchQueue(label: "com.shifthackz.aisdv1.coreml.runtime", qos: .userInitiated)
    private let interruptionLock = NSLock()
    private var interrupted = false
    private var cachedPipeline: StableDiffusionPipeline?
    private var cachedResourcesPath: String?
    private var cachedDisableSafety = false

    public init() {}

    public func generate(
        request: Request,
        onProgress: @escaping (Progress) -> Void,
        completion: @escaping (Response) -> Void
    ) {
        queue.async { [weak self] in
            guard let self else { return }
            self.setInterrupted(false)

            do {
                let resourcesURL = try Self.resolveResourcesURL(modelPath: request.modelPath)
                var pipeline = try self.pipeline(
                    resourcesURL: resourcesURL,
                    disableSafety: request.allowNsfw
                )
                try pipeline.loadResources()

                let startingImage = try request.startingImageBase64?.decodedCoreMlImage(
                    width: Int(request.width),
                    height: Int(request.height)
                )
                var configuration = StableDiffusionPipeline.Configuration(prompt: request.prompt)
                configuration.negativePrompt = request.negativePrompt
                configuration.startingImage = startingImage
                configuration.strength = startingImage == nil ? 1.0 : Self.normalizedStrength(request.strength)
                configuration.imageCount = 1
                configuration.stepCount = max(1, Int(request.samplingSteps))
                configuration.guidanceScale = request.cfgScale
                configuration.seed = Self.seed(from: request.seed)
                configuration.disableSafety = request.allowNsfw
                configuration.schedulerType = .dpmSolverMultistepScheduler

                let maxAttempts = request.allowNsfw ? 1 : 3
                for attempt in 0..<maxAttempts {
                    var attemptConfiguration = configuration
                    if attempt > 0 {
                        attemptConfiguration.seed = Self.retrySeed(
                            from: request.seed,
                            attempt: attempt
                        )
                    }

                    let images = try pipeline.generateImages(configuration: attemptConfiguration) { progress in
                        onProgress(
                            Progress(
                                current: Int32(progress.step + 1),
                                total: Int32(progress.stepCount)
                            )
                        )
                        return !self.isInterrupted()
                    }

                    guard !self.isInterrupted() else {
                        throw SiliconDiffusionCoreMLError.interrupted
                    }

                    if let image = images.compactMap({ $0 }).first {
                        completion(
                            Response(
                                imageBase64: try image.base64Jpeg(),
                                errorMessage: nil
                            )
                        )
                        return
                    }
                }

                throw SiliconDiffusionCoreMLError.emptyResult
            } catch {
                completion(
                    Response(
                        imageBase64: nil,
                        errorMessage: Self.errorMessage(for: error)
                    )
                )
            }
        }
    }

    public func interrupt() {
        setInterrupted(true)
    }

    private func pipeline(
        resourcesURL: URL,
        disableSafety: Bool
    ) throws -> StableDiffusionPipeline {
        let resourcesPath = resourcesURL.standardizedFileURL.path
        if let cachedPipeline, cachedResourcesPath == resourcesPath, cachedDisableSafety == disableSafety {
            return cachedPipeline
        }

        let configuration = MLModelConfiguration()
#if targetEnvironment(simulator)
        configuration.computeUnits = .cpuAndGPU
#else
        configuration.computeUnits = .cpuAndNeuralEngine
#endif

        let pipeline = try StableDiffusionPipeline(
            resourcesAt: resourcesURL,
            controlNet: [],
            configuration: configuration,
            disableSafety: disableSafety,
            reduceMemory: true
        )
        cachedPipeline = pipeline
        cachedResourcesPath = resourcesPath
        cachedDisableSafety = disableSafety
        return pipeline
    }

    private func setInterrupted(_ interrupted: Bool) {
        interruptionLock.lock()
        self.interrupted = interrupted
        interruptionLock.unlock()
    }

    private func isInterrupted() -> Bool {
        interruptionLock.lock()
        defer { interruptionLock.unlock() }
        return interrupted
    }
}

@available(iOS 16.2, *)
private extension SiliconDiffusionCoreMLGenerator {
    static func resolveResourcesURL(modelPath: String) throws -> URL {
        let modelURL = URL(fileURLWithPath: modelPath, isDirectory: true)
        if let resourcesURL = findResourcesURL(in: modelURL) {
            return resourcesURL
        }

        let archiveURL = modelURL.appendingPathComponent("model.zip", isDirectory: false)
        guard FileManager.default.fileExists(atPath: archiveURL.path) else {
            throw SiliconDiffusionCoreMLError.modelResourcesNotFound(modelPath)
        }

        let extractedURL = modelURL.appendingPathComponent("extracted", isDirectory: true)
        if findResourcesURL(in: extractedURL) == nil {
            if FileManager.default.fileExists(atPath: extractedURL.path) {
                try FileManager.default.removeItem(at: extractedURL)
            }
            try FileManager.default.createDirectory(
                at: extractedURL,
                withIntermediateDirectories: true
            )
            try FileManager.default.unzipItem(at: archiveURL, to: extractedURL)
        }

        if let resourcesURL = findResourcesURL(in: extractedURL) {
            return resourcesURL
        }

        throw SiliconDiffusionCoreMLError.invalidModelArchive
    }

    static func findResourcesURL(in rootURL: URL) -> URL? {
        let candidates = [
            rootURL,
            rootURL.appendingPathComponent("Resources", isDirectory: true),
            rootURL.appendingPathComponent("extracted", isDirectory: true),
            rootURL
                .appendingPathComponent("extracted", isDirectory: true)
                .appendingPathComponent("Resources", isDirectory: true),
        ]

        if let direct = candidates.first(where: isStableDiffusionResourcesDirectory) {
            return direct
        }

        guard let enumerator = FileManager.default.enumerator(
            at: rootURL,
            includingPropertiesForKeys: [.isDirectoryKey],
            options: [.skipsHiddenFiles]
        ) else {
            return nil
        }

        let rootDepth = rootURL.pathComponents.count
        for case let url as URL in enumerator {
            if url.pathComponents.count - rootDepth > 4 {
                enumerator.skipDescendants()
                continue
            }
            if isStableDiffusionResourcesDirectory(url) {
                return url
            }
        }

        return nil
    }

    static func isStableDiffusionResourcesDirectory(_ url: URL) -> Bool {
        let fileManager = FileManager.default
        let required = [
            "TextEncoder.mlmodelc",
            "VAEDecoder.mlmodelc",
            "vocab.json",
            "merges.txt",
        ]
        guard required.allSatisfy({ fileManager.fileExists(atPath: url.appendingPathComponent($0).path) }) else {
            return false
        }

        let unet = url.appendingPathComponent("Unet.mlmodelc").path
        let unetChunk1 = url.appendingPathComponent("UnetChunk1.mlmodelc").path
        let unetChunk2 = url.appendingPathComponent("UnetChunk2.mlmodelc").path
        return fileManager.fileExists(atPath: unet)
            || (
                fileManager.fileExists(atPath: unetChunk1)
                    && fileManager.fileExists(atPath: unetChunk2)
            )
    }

    static func seed(from value: String) -> UInt32 {
        let trimmed = value.trimmingCharacters(in: .whitespacesAndNewlines)
        guard let parsed = Int64(trimmed), parsed >= 0 else {
            return UInt32.random(in: UInt32.min...UInt32.max)
        }
        return UInt32(truncatingIfNeeded: parsed)
    }

    static func retrySeed(from value: String, attempt: Int) -> UInt32 {
        let trimmed = value.trimmingCharacters(in: .whitespacesAndNewlines)
        guard let parsed = Int64(trimmed), parsed >= 0 else {
            return UInt32.random(in: UInt32.min...UInt32.max)
        }
        return UInt32(truncatingIfNeeded: parsed) &+ UInt32(attempt)
    }

    static func normalizedStrength(_ value: Float) -> Float {
        min(max(value, 0.01), 0.99)
    }

    static func errorMessage(for error: Error) -> String {
        if let pipelineError = error as? PipelineError,
           pipelineError == .startingImageProvidedWithoutEncoder {
            return SiliconDiffusionCoreMLError.imageToImageUnsupported.errorDescription ?? error.localizedDescription
        }
        return error.localizedDescription
    }
}

@available(iOS 16.2, *)
private extension CGImage {
    func base64Jpeg() throws -> String {
        guard let data = UIImage(cgImage: self).jpegData(compressionQuality: 0.95) else {
            throw SiliconDiffusionCoreMLError.imageEncodingFailed
        }
        return data.base64EncodedString()
    }
}

@available(iOS 16.2, *)
private extension String {
    func decodedCoreMlImage(width: Int, height: Int) throws -> CGImage {
        let normalized = substringAfterBase64Marker()
            .filter { !$0.isWhitespace }
        guard
            let data = Data(base64Encoded: normalized, options: .ignoreUnknownCharacters),
            let image = UIImage(data: data)
        else {
            throw SiliconDiffusionCoreMLError.invalidInputImage
        }
        return try image.resizedCoreMlImage(width: width, height: height)
    }

    func substringAfterBase64Marker() -> String {
        guard let range = range(of: "base64,") else {
            return self
        }
        return String(self[range.upperBound...])
    }
}

@available(iOS 16.2, *)
private extension UIImage {
    func resizedCoreMlImage(width: Int, height: Int) throws -> CGImage {
        let targetSize = CGSize(
            width: max(1, width),
            height: max(1, height)
        )
        let format = UIGraphicsImageRendererFormat()
        format.scale = 1
        format.opaque = true
        let renderer = UIGraphicsImageRenderer(size: targetSize, format: format)
        let image = renderer.image { _ in
            draw(in: CGRect(origin: .zero, size: targetSize))
        }
        guard let cgImage = image.cgImage else {
            throw SiliconDiffusionCoreMLError.invalidInputImage
        }
        return cgImage
    }
}

private enum SiliconDiffusionCoreMLError: LocalizedError {
    case modelResourcesNotFound(String)
    case invalidModelArchive
    case emptyResult
    case invalidInputImage
    case imageToImageUnsupported
    case imageEncodingFailed
    case interrupted

    var errorDescription: String? {
        switch self {
        case .modelResourcesNotFound(let path):
            return "Core ML model resources were not found at \(path)."
        case .invalidModelArchive:
            return "Downloaded Core ML model archive does not contain supported Stable Diffusion resources."
        case .emptyResult:
            return "Silicon Diffusion Core ML did not return an image."
        case .invalidInputImage:
            return "Selected input image could not be decoded for Core ML img2img."
        case .imageToImageUnsupported:
            return "Selected Core ML model does not contain VAE encoder resources required for img2img."
        case .imageEncodingFailed:
            return "Unable to encode generated image."
        case .interrupted:
            return "Silicon Diffusion Core ML generation was interrupted."
        }
    }
}
