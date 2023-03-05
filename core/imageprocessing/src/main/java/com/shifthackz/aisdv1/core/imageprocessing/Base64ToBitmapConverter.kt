package com.shifthackz.aisdv1.core.imageprocessing

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter.Input
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter.Output
import com.shifthackz.aisdv1.core.imageprocessing.contract.RxImageProcessor
import com.shifthackz.aisdv1.core.imageprocessing.utils.base64ToImage
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single

typealias Base64ToBitmapProcessor = RxImageProcessor<Input, Output>

class Base64ToBitmapConverter(
    private val processingScheduler: Scheduler,
) : Base64ToBitmapProcessor {

    override operator fun invoke(input: Input): Single<Output> = Single
        .create { emitter ->
            convert(input).fold(
                onSuccess = emitter::onSuccess,
                onFailure = emitter::onError,
            )
        }
        .subscribeOn(processingScheduler)

    private fun convert(input: Input): Result<Output> = runCatching {
        Output(base64ToImage(input.base64ImageString))
    }

    data class Input(val base64ImageString: String)
    data class Output(val bitmap: Bitmap)
}
