package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.Supporter

/**
 * Defines the `SupportersDataSource` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface SupportersDataSource {

    /**
     * Defines the `Local` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Local : SupportersDataSource {
        /**
         * Performs the SDAI side effect handled by `save`.
         *
         * @param data data value consumed by the API.
         * @author Dmitriy Moroz
         */
        suspend fun save(data: List<Supporter>)
        /**
         * Loads SDAI data through `getAll`.
         *
         * @return Result produced by `getAll`.
         * @author Dmitriy Moroz
         */
        suspend fun getAll(): List<Supporter>
    }
}
