package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.ReportReason

/**
 * Defines the `ReportDataSource` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface ReportDataSource {

    /**
     * Defines the `Remote` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Remote : ReportDataSource {

        /**
         * Executes the `send` step in the SDAI domain layer.
         *
         * @param text text value consumed by the API.
         * @param reason reason value consumed by the API.
         * @param image image value consumed by the API.
         * @param source source value consumed by the API.
         * @param model model value consumed by the API.
         * @author Dmitriy Moroz
         */
        suspend fun send(
            text: String,
            reason: ReportReason,
            image: String,
            source: String,
            model: String,
        )
    }
}
