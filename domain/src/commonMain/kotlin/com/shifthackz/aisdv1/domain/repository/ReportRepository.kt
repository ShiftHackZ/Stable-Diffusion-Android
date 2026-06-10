package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.ReportReason

/**
 * Defines the `ReportRepository` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface ReportRepository {
    /**
     * Executes the `send` step in the SDAI domain layer.
     *
     * @param text text value consumed by the API.
     * @param reason reason value consumed by the API.
     * @param image image value consumed by the API.
     * @author Dmitriy Moroz
     */
    suspend fun send(text: String, reason: ReportReason, image: String)
}
