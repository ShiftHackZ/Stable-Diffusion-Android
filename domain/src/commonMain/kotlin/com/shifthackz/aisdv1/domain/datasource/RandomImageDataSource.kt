package com.shifthackz.aisdv1.domain.datasource

sealed interface RandomImageDataSource {

    interface Remote : RandomImageDataSource {
        suspend fun fetch(): ByteArray
    }
}
