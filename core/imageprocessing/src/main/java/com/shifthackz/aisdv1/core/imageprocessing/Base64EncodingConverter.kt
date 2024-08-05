package com.shifthackz.aisdv1.core.imageprocessing

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.imageprocessing.Base64EncodingConverter.Input
import com.shifthackz.aisdv1.core.imageprocessing.Base64EncodingConverter.Output
import com.shifthackz.aisdv1.core.imageprocessing.contract.RxImageProcessor
import com.shifthackz.aisdv1.core.imageprocessing.utils.base64DefaultToNoWrap
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single

private typealias Base64EncodingProcessor = RxImageProcessor<Input, Output>

class Base64EncodingConverter(
    private val processingScheduler: Scheduler,
) : Base64EncodingProcessor {

    override fun invoke(input: Input): Single<Output> = Single
        .create { emitter ->
            convert(input).fold(
                onSuccess = emitter::onSuccess,
                onFailure = emitter::onError,
            )
        }
        .onErrorReturn { t ->
            errorLog(t)
            Output(input.base64)
        }
        .subscribeOn(processingScheduler)

    private fun convert(input: Input): Result<Output> = runCatching {
        Output(base64DefaultToNoWrap(input.base64))
    }

    data class Input(val base64: String)
    data class Output(val base64: String)
}
