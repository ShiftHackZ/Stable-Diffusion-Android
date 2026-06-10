package com.shifthackz.aisdv1.core.imageprocessing

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.imageprocessing.Base64EncodingConverter.Input
import com.shifthackz.aisdv1.core.imageprocessing.Base64EncodingConverter.Output
import com.shifthackz.aisdv1.core.imageprocessing.utils.base64DefaultToNoWrap

class Base64EncodingConverter {

    operator fun invoke(input: Input): Output =
        convert(input).getOrElse { t ->
            errorLog(t)
            Output(input.base64)
        }

    private fun convert(input: Input): Result<Output> = runCatching {
        Output(base64DefaultToNoWrap(input.base64))
    }

    data class Input(val base64: String)
    data class Output(val base64: String)
}
