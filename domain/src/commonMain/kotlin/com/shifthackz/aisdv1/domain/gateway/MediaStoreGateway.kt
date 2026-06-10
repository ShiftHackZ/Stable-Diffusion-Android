package com.shifthackz.aisdv1.domain.gateway

import com.shifthackz.aisdv1.domain.entity.MediaStoreInfo

/**
 * Defines the `MediaStoreGateway` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface MediaStoreGateway {
    /**
     * Executes the `exportToFile` step in the SDAI domain layer.
     *
     * @param fileName file name value consumed by the API.
     * @param content content value consumed by the API.
     * @author Dmitriy Moroz
     */
    fun exportToFile(fileName: String, content: ByteArray)
    /**
     * Loads SDAI data through `getInfo`.
     *
     * @return Result produced by `getInfo`.
     * @author Dmitriy Moroz
     */
    fun getInfo(): MediaStoreInfo
}
