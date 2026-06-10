package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.LoRA

interface LorasRepository {
    suspend fun fetchLoras()
    suspend fun fetchAndGetLoras(): List<LoRA>
    suspend fun getLoras(): List<LoRA>
}
