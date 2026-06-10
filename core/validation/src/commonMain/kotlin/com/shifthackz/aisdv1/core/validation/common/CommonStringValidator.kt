package com.shifthackz.aisdv1.core.validation.common

import com.shifthackz.aisdv1.core.validation.ValidationResult

/**
 * Defines the `CommonStringValidator` contract for the SDAI validation layer.
 *
 * @author Dmitriy Moroz
 */
interface CommonStringValidator {

    /**
     * Executes the `invoke` step in the SDAI validation layer.
     *
     * @param input input value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    operator fun invoke(input: String?) : ValidationResult<Error>

    /**
     * Defines the `Error` contract for the SDAI validation layer.
     *
     * @author Dmitriy Moroz
     */
    sealed interface Error {
        /**
         * Provides the `Empty` singleton used by the SDAI validation layer.
         *
         * @author Dmitriy Moroz
         */
        data object Empty : Error
    }
}
