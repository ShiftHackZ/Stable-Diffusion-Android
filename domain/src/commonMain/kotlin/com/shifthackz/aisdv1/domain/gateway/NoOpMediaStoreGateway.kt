package com.shifthackz.aisdv1.domain.gateway

import com.shifthackz.aisdv1.domain.entity.MediaStoreInfo

object NoOpMediaStoreGateway : MediaStoreGateway {
    override fun exportToFile(fileName: String, content: ByteArray) = Unit

    override fun getInfo(): MediaStoreInfo = MediaStoreInfo()
}
