package com.shifthackz.aisdv1.domain.repository

interface RandomImageRepository {
    suspend fun fetchAndGet(): ByteArray
}
