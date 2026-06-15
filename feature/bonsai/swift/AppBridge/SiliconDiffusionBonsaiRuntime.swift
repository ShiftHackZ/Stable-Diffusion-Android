@preconcurrency import AiSdPresentation
import Foundation
import SiliconDiffusionBonsai

@available(iOS 17.0, *)
final class SiliconDiffusionBonsaiRuntimeAdapter: NSObject, SiliconDiffusionBonsaiRuntime {
    private let generator = SiliconDiffusionBonsaiGenerator()

    func generate(
        request: SiliconDiffusionBonsaiRequest,
        onProgress: @escaping @Sendable (SiliconDiffusionBonsaiProgress) -> Void,
        completion: @escaping @Sendable (SiliconDiffusionBonsaiResponse) -> Void
    ) {
        print(
            "[Bonsai] swift bridge request modelPath=\(request.modelPath) " +
                "size=\(request.width)x\(request.height) steps=\(request.samplingSteps) " +
                "cfg=\(request.cfgScale) seedBlank=\(request.seed.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty) " +
                "promptChars=\(request.prompt.count) negativeChars=\(request.negativePrompt.count)"
        )
        generator.generate(
            request: SiliconDiffusionBonsaiGenerator.Request(
                modelPath: request.modelPath,
                prompt: request.prompt,
                negativePrompt: request.negativePrompt,
                samplingSteps: request.samplingSteps,
                cfgScale: request.cfgScale,
                width: request.width,
                height: request.height,
                seed: request.seed,
                allowNsfw: request.allowNsfw
            ),
            onProgress: { progress in
                onProgress(
                    SiliconDiffusionBonsaiProgress(
                        current: progress.current,
                        total: progress.total
                    )
                )
            },
            completion: { response in
                let byteCount = response.imageBase64.flatMap { Data(base64Encoded: $0)?.count } ?? 0
                print(
                    "[Bonsai] swift bridge completion imageBytes=\(byteCount) " +
                        "error=\(response.errorMessage ?? "nil")"
                )
                completion(
                    SiliconDiffusionBonsaiResponse(
                        imageBase64: response.imageBase64,
                        errorMessage: response.errorMessage
                    )
                )
            }
        )
    }

    func interrupt() {
        print("[Bonsai] swift bridge interrupt")
        generator.interrupt()
    }
}

@available(iOS 17.0, *)
private enum SiliconDiffusionBonsaiRuntimeBootstrap {
    static func register() {
        print("[Bonsai] swift bridge register")
        SiliconDiffusionBonsaiRuntimeRegistry.shared.register(
            runtime: SiliconDiffusionBonsaiRuntimeAdapter()
        )
    }
}

enum SiliconDiffusionBonsaiRuntimeRegistration {
    static func registerIfAvailable() {
        guard #available(iOS 17.0, *) else {
            return
        }
        SiliconDiffusionBonsaiRuntimeBootstrap.register()
    }
}
