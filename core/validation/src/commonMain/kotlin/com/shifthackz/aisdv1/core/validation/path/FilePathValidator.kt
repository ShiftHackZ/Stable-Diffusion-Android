package com.shifthackz.aisdv1.core.validation.path

import com.shifthackz.aisdv1.core.validation.ValidationResult

/**
 * Defines the `FilePathValidator` contract for the SDAI validation layer.
 *
 * @author Dmitriy Moroz
 */
interface FilePathValidator {

    /**
     * Executes the `invoke` step in the SDAI validation layer.
     *
     * @param input input value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    operator fun invoke(input: String?): ValidationResult<Error>

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
        /**
         * Provides the `Invalid` singleton used by the SDAI validation layer.
         *
         * @author Dmitriy Moroz
         */
        data object Invalid : Error
    }
}
