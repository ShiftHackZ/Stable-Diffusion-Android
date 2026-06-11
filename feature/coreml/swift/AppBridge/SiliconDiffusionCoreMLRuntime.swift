import AiSdPresentation
import Foundation
import SiliconDiffusionCoreML

@available(iOS 16.2, *)
final class SiliconDiffusionCoreMLRuntimeAdapter: NSObject, SiliconDiffusionCoreMlRuntime {
    private let generator = SiliconDiffusionCoreMLGenerator()

    func generate(
        request: SiliconDiffusionCoreMlRequest,
        onProgress: @escaping (SiliconDiffusionCoreMlProgress) -> Void,
        completion: @escaping (SiliconDiffusionCoreMlResponse) -> Void
    ) {
        generator.generate(
            request: SiliconDiffusionCoreMLGenerator.Request(
                modelPath: request.modelPath,
                prompt: request.prompt,
                negativePrompt: request.negativePrompt,
                samplingSteps: request.samplingSteps,
                cfgScale: request.cfgScale,
                width: request.width,
                height: request.height,
                seed: request.seed,
                allowNsfw: request.allowNsfw,
                startingImageBase64: request.startingImageBase64,
                strength: request.strength
            ),
            onProgress: { progress in
                onProgress(
                    SiliconDiffusionCoreMlProgress(
                        current: progress.current,
                        total: progress.total
                    )
                )
            },
            completion: { response in
                completion(
                    SiliconDiffusionCoreMlResponse(
                        imageBase64: response.imageBase64,
                        errorMessage: response.errorMessage
                    )
                )
            }
        )
    }

    func interrupt() {
        generator.interrupt()
    }
}

@available(iOS 16.2, *)
private enum SiliconDiffusionCoreMLRuntimeBootstrap {
    static func register() {
        SiliconDiffusionCoreMlRuntimeRegistry.shared.register(
            runtime: SiliconDiffusionCoreMLRuntimeAdapter()
        )
    }
}

enum SiliconDiffusionCoreMLRuntimeRegistration {
    static func registerIfAvailable() {
        guard #available(iOS 16.2, *) else {
            return
        }
        SiliconDiffusionCoreMLRuntimeBootstrap.register()
    }
}
