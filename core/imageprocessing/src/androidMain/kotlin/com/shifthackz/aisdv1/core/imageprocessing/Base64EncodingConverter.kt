package com.shifthackz.aisdv1.core.imageprocessing

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.imageprocessing.utils.base64DefaultToNoWrap

/**
 * Coordinates `Base64EncodingConverter` behavior in the SDAI image processing layer.
 *
 * @author Dmitriy Moroz
 */
class Base64EncodingConverter {

    /**
     * Executes the `invoke` step in the SDAI image processing layer.
     *
     * @param input input value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    operator fun invoke(input: Input): Output =
        convert(input).getOrElse { t ->
            errorLog(t)
            Output(input.base64)
        }

    /**
     * Executes the `convert` step in the SDAI image processing layer.
     *
     * @param input input value consumed by the API.
     * @return Result produced by `convert`.
     * @author Dmitriy Moroz
     */
    private fun convert(input: Input): Result<Output> = runCatching {
        Output(base64DefaultToNoWrap(input.base64))
    }

    /**
     * Carries `Input` data through the SDAI image processing layer.
     *
     * @param base64 Base64 image payload used by the operation.
     * @author Dmitriy Moroz
     */
    data class Input(val base64: String)
    /**
     * Carries `Output` data through the SDAI image processing layer.
     *
     * @param base64 Base64 image payload used by the operation.
     * @author Dmitriy Moroz
     */
    data class Output(val base64: String)
}
