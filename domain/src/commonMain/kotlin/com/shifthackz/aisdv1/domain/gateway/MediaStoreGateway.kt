package com.shifthackz.aisdv1.domain.gateway

import com.shifthackz.aisdv1.domain.entity.MediaStoreInfo

interface MediaStoreGateway {
    fun exportToFile(fileName: String, content: ByteArray)
    fun getInfo(): MediaStoreInfo
}
