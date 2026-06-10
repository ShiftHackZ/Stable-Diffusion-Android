package com.shifthackz.aisdv1.network.api.imagecdn

interface ImageCdnApi {

    suspend fun fetchRandomImageBytes(): ByteArray
}
