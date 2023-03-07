package com.shifthackz.aisdv1.core.imageprocessing

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.imageprocessing.BitmapToBase64Converter.Input
import com.shifthackz.aisdv1.core.imageprocessing.BitmapToBase64Converter.Output
import com.shifthackz.aisdv1.core.imageprocessing.contract.RxImageProcessor
import com.shifthackz.aisdv1.core.imageprocessing.utils.bitmapToBase64
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single

private typealias BitmapToBase64Processor = RxImageProcessor<Input, Output>

class BitmapToBase64Converter(
    private val processingScheduler: Scheduler,
) : BitmapToBase64Processor {

    override operator fun invoke(input: Input): Single<Output> = Single
        .create { emitter ->
            convert(input).fold(
                onSuccess = emitter::onSuccess,
                onFailure = emitter::onError,
            )
        }
        .subscribeOn(processingScheduler)

    private fun convert(input: Input): Result<Output> = runCatching {
        Output(bitmapToBase64(input.bitmap))
    }

    data class Input(val bitmap: Bitmap)
    data class Output(val base64ImageString: String)
}
