import Foundation

@available(iOS 17.0, macOS 14.0, *)
public final class SiliconDiffusionBonsaiGenerator: @unchecked Sendable {
    public struct Request: Sendable {
        public let modelPath: String
        public let prompt: String
        public let negativePrompt: String
        public let samplingSteps: Int32
        public let cfgScale: Float
        public let width: Int32
        public let height: Int32
        public let seed: String
        public let allowNsfw: Bool

        public init(
            modelPath: String,
            prompt: String,
            negativePrompt: String,
            samplingSteps: Int32,
            cfgScale: Float,
            width: Int32,
            height: Int32,
            seed: String,
            allowNsfw: Bool
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
        }
    }

    public struct Progress: Sendable {
        public let current: Int32
        public let total: Int32

        public init(current: Int32, total: Int32) {
            self.current = current
            self.total = total
        }
    }

    public struct Response: Sendable {
        public let imageBase64: String?
        public let errorMessage: String?

        public init(imageBase64: String?, errorMessage: String?) {
            self.imageBase64 = imageBase64
            self.errorMessage = errorMessage
        }
    }

    private let queue = DispatchQueue(label: "com.shifthackz.aisdv1.bonsai.runtime", qos: .userInitiated)
    private let interruptionLock = NSLock()
    private var interrupted = false

    public init() {}

    public func generate(
        request: Request,
        onProgress: @escaping @Sendable (Progress) -> Void,
        completion: @escaping @Sendable (Response) -> Void
    ) {
        queue.async { [weak self] in
            guard let self else { return }
            self.setInterrupted(false)

            do {
                print("[Bonsai] generator begin")
                try BonsaiRuntimeEnvironment.configureBeforeMlx()
                BonsaiMlxMemory.configureForInference()
                try BonsaiRequestValidator.validate(request: request)
                print("[Bonsai] generator request validated")
                let layout = try BonsaiModelLayout.resolve(modelPath: request.modelPath)
                print(
                    "[Bonsai] generator layout resolved root=\(layout.rootURL.path) " +
                        "text=\(layout.textEncoderURL.lastPathComponent) " +
                        "transformer=\(layout.packedTransformerURL.lastPathComponent) " +
                        "vae=\(layout.vaeURL.lastPathComponent)"
                )
                let pipeline = BonsaiMlxPipeline(layout: layout)
                let image = try self.generateValidatedImage(
                    request: request,
                    layout: layout,
                    pipeline: pipeline,
                    onProgress: onProgress
                )
                completion(
                    Response(
                        imageBase64: image,
                        errorMessage: nil
                    )
                )
            } catch {
                print("[Bonsai] generator failed \(Self.errorMessage(for: error))")
                completion(
                    Response(
                        imageBase64: nil,
                        errorMessage: Self.errorMessage(for: error)
                    )
                )
            }
        }
    }

    private func generateValidatedImage(
        request: Request,
        layout: BonsaiModelLayout,
        pipeline: BonsaiMlxPipeline,
        onProgress: @escaping @Sendable (Progress) -> Void
    ) throws -> String {
        let stepCount = max(1, Int(request.samplingSteps))
        let total = Int32(stepCount)
        let seedProvided = !request.seed.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
        let maxAttempts = seedProvided ? 1 : 2
        var lastError: Error?

        for attempt in 1...maxAttempts {
            onProgress(Progress(current: 0, total: total))

            guard !isInterrupted() else {
                throw BonsaiRuntimeError.interrupted
            }

            let seed = try BonsaiSeed.parse(request.seed)
            print(
                "[Bonsai] attempt \(attempt)/\(maxAttempts) seed=\(seed) " +
                    "size=\(request.width)x\(request.height) steps=\(stepCount) cfg=\(request.cfgScale)"
            )

            do {
                return try pipeline.generate(
                    input: BonsaiGenerationInput(
                        layout: layout,
                        prompt: request.prompt,
                        negativePrompt: request.negativePrompt,
                        steps: stepCount,
                        guidance: request.cfgScale,
                        width: Int(request.width),
                        height: Int(request.height),
                        seed: seed
                    ),
                    onProgress: onProgress,
                    shouldContinue: { !self.isInterrupted() }
                )
            } catch BonsaiRuntimeError.invalidOutputImage(let reason) where !seedProvided && attempt < maxAttempts {
                lastError = BonsaiRuntimeError.invalidOutputImage(reason)
                print("[Bonsai] retrying after invalid output: \(reason)")
                BonsaiMlxMemory.reclaimCache()
            } catch {
                throw error
            }
        }

        throw lastError ?? BonsaiRuntimeError.imageEncodingFailed
    }

    public func interrupt() {
        print("[Bonsai] generator interrupt requested")
        setInterrupted(true)
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

private enum BonsaiRuntimeEnvironment {
    static func configureBeforeMlx() throws {
        #if os(iOS) && targetEnvironment(simulator)
        throw BonsaiRuntimeError.runtimeUnavailable(
            "iOS Simulator Metal does not support the MLX allocator. Run Bonsai Image on a real iOS device."
        )
        #endif
    }
}

@available(iOS 17.0, macOS 14.0, *)
enum BonsaiRequestValidator {
    static func validate(request: SiliconDiffusionBonsaiGenerator.Request) throws {
        guard !request.prompt.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else {
            throw BonsaiRuntimeError.emptyPrompt
        }

        guard request.width > 0, request.height > 0 else {
            throw BonsaiRuntimeError.invalidSize
        }

        guard request.width % 32 == 0, request.height % 32 == 0 else {
            throw BonsaiRuntimeError.invalidSize
        }

        guard FileManager.default.fileExists(atPath: request.modelPath) else {
            throw BonsaiRuntimeError.modelResourcesNotFound(request.modelPath)
        }
    }
}

@available(iOS 17.0, macOS 14.0, *)
private extension SiliconDiffusionBonsaiGenerator {
    static func errorMessage(for error: Error) -> String {
        if let error = error as? BonsaiRuntimeError {
            return error.localizedDescription
        }
        return error.localizedDescription
    }
}
