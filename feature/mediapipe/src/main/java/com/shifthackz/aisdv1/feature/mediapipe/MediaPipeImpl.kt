package com.shifthackz.aisdv1.feature.mediapipe

import android.content.Context
import android.graphics.Bitmap
import com.google.mediapipe.framework.image.BitmapExtractor
import com.google.mediapipe.tasks.vision.imagegenerator.ImageGenerator
import com.google.mediapipe.tasks.vision.imagegenerator.ImageGenerator.ImageGeneratorOptions
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.mediapipe.MediaPipe
import io.reactivex.rxjava3.core.Single

internal class MediaPipeImpl(
    private val context: Context,
    private val fileProviderDescriptor: FileProviderDescriptor,
): MediaPipe {

    private var imageGenerator: ImageGenerator? = null

    override fun process(payload: TextToImagePayload): Single<Bitmap> = Single.create { emitter ->
            try {
                initialize()
                println("Generating...")
                val result = imageGenerator?.generate(
                    payload.prompt,
                    payload.samplingSteps,
                    payload.seed.toIntOrNull() ?: 0,
                )
                println("Extracting bitmap...")
                val bitmap = BitmapExtractor.extract(result?.generatedImage())
                println("bitmap = $bitmap, ${bitmap.width}X${bitmap.height}")
                close()
                if (!emitter.isDisposed) emitter.onSuccess(bitmap)
            } catch (e: Exception) {
                close()
                if (!emitter.isDisposed) emitter.onError(e)
            }
    }

    private fun initialize(): ImageGenerator {
        val options = ImageGeneratorOptions.builder()
            .setImageGeneratorModelDirectory(fileProviderDescriptor.mediaPipeDirPath)
            .build()

        val generator = ImageGenerator.createFromOptions(context, options)
        imageGenerator = generator
        println("Initialized successfully! Path: ${fileProviderDescriptor.mediaPipeDirPath}")
        return generator
    }

    private fun close() = runCatching {
        println("Closing...")
        imageGenerator?.close()
        imageGenerator = null
        println("Session closed!")
    }
}
