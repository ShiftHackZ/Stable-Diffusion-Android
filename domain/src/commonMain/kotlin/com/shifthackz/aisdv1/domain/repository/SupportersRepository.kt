package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.Supporter

interface SupportersRepository {
    suspend fun fetchSupporters()
    suspend fun fetchAndGetSupporters(): List<Supporter>
    suspend fun getSupporters(): List<Supporter>
}
