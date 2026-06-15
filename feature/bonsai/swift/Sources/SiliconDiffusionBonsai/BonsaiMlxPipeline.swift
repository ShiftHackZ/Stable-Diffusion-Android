import Foundation
import MLX

final class BonsaiMlxPipeline {
    private let layout: BonsaiModelLayout

    init(layout: BonsaiModelLayout) {
        self.layout = layout
    }

    func generate(
        input: BonsaiGenerationInput,
        onProgress: (SiliconDiffusionBonsaiGenerator.Progress) -> Void,
        shouldContinue: () -> Bool
    ) throws -> String {
        guard shouldContinue() else {
            throw BonsaiRuntimeError.interrupted
        }

        print("[Bonsai] phase text_encoder begin")
        let encodedPrompts = try Self.encodePrompts(input: input, layout: layout)
        print(
            "[Bonsai] phase text_encoder done promptShape=\(encodedPrompts.prompt.embeddings.shape) " +
                "negativeShape=\(encodedPrompts.negativePrompt?.embeddings.shape.description ?? "nil")"
        )
        BonsaiMlxMemory.reclaimCache()

        print("[Bonsai] phase latents begin")
        var latents = BonsaiLatentCreator.preparePackedLatents(
            seed: input.seed,
            height: input.height,
            width: input.width
        )
        print(
            "[Bonsai] phase latents done valuesShape=\(latents.values.shape) idsShape=\(latents.ids.shape) " +
                "latentGrid=\(latents.latentWidth)x\(latents.latentHeight)"
        )
        let scheduler = BonsaiFlowMatchEulerScheduler(
            imageSeqLen: (input.height / 16) * (input.width / 16),
            steps: input.steps
        )
        let total = Int32(scheduler.timesteps.count)
        print(
            "[Bonsai] scheduler timesteps=\(scheduler.timesteps.count) " +
                "first=\(scheduler.timesteps.first ?? -1) last=\(scheduler.timesteps.last ?? -1)"
        )

        try autoreleasepool {
            print("[Bonsai] phase transformer load begin")
            let transformer = try BonsaiFluxTransformer(layout: layout)
            print("[Bonsai] phase transformer load done")
            BonsaiMlxMemory.reclaimCache()

            for index in scheduler.timesteps.indices {
                guard shouldContinue() else {
                    throw BonsaiRuntimeError.interrupted
                }

                print("[Bonsai] step \(index + 1)/\(scheduler.timesteps.count) begin timestep=\(scheduler.timesteps[index])")
                let condNoise = transformer(
                    hiddenStates: latents.values,
                    encoderHiddenStates: encodedPrompts.prompt.embeddings,
                    timestep: scheduler.timesteps[index],
                    imageIds: latents.ids,
                    textIds: encodedPrompts.prompt.textIds
                )
                let noise: MLXArray
                if let negativePrompt = encodedPrompts.negativePrompt {
                    let uncondNoise = transformer(
                        hiddenStates: latents.values,
                        encoderHiddenStates: negativePrompt.embeddings,
                        timestep: scheduler.timesteps[index],
                        imageIds: latents.ids,
                        textIds: negativePrompt.textIds
                    )
                    noise = uncondNoise + MLXArray(input.guidance, dtype: condNoise.dtype) * (condNoise - uncondNoise)
                } else {
                    noise = condNoise
                }
                latents = BonsaiLatents(
                    values: scheduler.step(noise: noise, timestep: index, latents: latents.values),
                    ids: latents.ids,
                    latentHeight: latents.latentHeight,
                    latentWidth: latents.latentWidth
                )
                eval(latents.values)
                BonsaiMlxMemory.reclaimCache()
                print("[Bonsai] step \(index + 1)/\(scheduler.timesteps.count) done latentsShape=\(latents.values.shape)")
                onProgress(
                    SiliconDiffusionBonsaiGenerator.Progress(
                        current: Int32(index + 1),
                        total: total
                    )
                )
            }
        }
        BonsaiMlxMemory.reclaimCache()
        print("[Bonsai] phase transformer done")

        let packed = latents.values
            .reshaped(1, latents.latentHeight, latents.latentWidth, latents.values.shape[2])
            .transposed(0, 3, 1, 2)
        print("[Bonsai] phase vae begin packedShape=\(packed.shape)")
        return try autoreleasepool {
            let vae = try BonsaiFluxVAE(layout: layout)
            print("[Bonsai] phase vae load done")
            BonsaiMlxMemory.reclaimCache()
            let decoded = vae.decodePacked(packed)
            eval(decoded)
            BonsaiMlxMemory.reclaimCache()
            print("[Bonsai] phase vae decode done decodedShape=\(decoded.shape)")
            return try BonsaiImageEncoder.base64Jpeg(from: decoded)
        }
    }

    private static func encodePrompts(
        input: BonsaiGenerationInput,
        layout: BonsaiModelLayout
    ) throws -> (prompt: BonsaiPromptEmbeddings, negativePrompt: BonsaiPromptEmbeddings?) {
        try autoreleasepool {
            let textEncoder = try BonsaiTextEncoder(layout: layout)
            let prompt = try textEncoder.encode(prompt: input.prompt)
            let negativePrompt: BonsaiPromptEmbeddings? = input.guidance > 1.0
                ? try textEncoder.encode(prompt: input.negativePrompt.isEmpty ? " " : input.negativePrompt)
                : nil
            if let negativePrompt {
                eval(prompt.embeddings, prompt.textIds, negativePrompt.embeddings, negativePrompt.textIds)
            } else {
                eval(prompt.embeddings, prompt.textIds)
            }
            return (prompt, negativePrompt)
        }
    }
}
