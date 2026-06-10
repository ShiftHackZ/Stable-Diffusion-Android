package com.shifthackz.aisdv1.domain.gateway

import com.shifthackz.aisdv1.domain.entity.MediaStoreInfo

/**
 * Provides the `NoOpMediaStoreGateway` singleton used by the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpMediaStoreGateway : MediaStoreGateway {
    /**
     * Executes the `exportToFile` step in the SDAI domain layer.
     *
     * @param fileName file name value consumed by the API.
     * @param content content value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun exportToFile(fileName: String, content: ByteArray) = Unit

    /**
     * Loads SDAI data through `getInfo`.
     *
     * @author Dmitriy Moroz
     */
    override fun getInfo(): MediaStoreInfo = MediaStoreInfo()
}
