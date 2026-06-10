package com.shifthackz.aisdv1.domain.datasource

/**
 * Defines the `RandomImageDataSource` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface RandomImageDataSource {

    /**
     * Defines the `Remote` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Remote : RandomImageDataSource {
        /**
         * Loads SDAI data through `fetch`.
         *
         * @return Result produced by `fetch`.
         * @author Dmitriy Moroz
         */
        suspend fun fetch(): ByteArray
    }
}
