package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import kotlinx.coroutines.flow.Flow

interface DownloadableModelRepository {
    fun download(id: String, url: String): Flow<DownloadState>
    suspend fun delete(id: String)
    suspend fun getAllOnnx(): List<LocalAiModel>
    suspend fun getAllMediaPipe(): List<LocalAiModel>
    fun observeAllOnnx(): Flow<List<LocalAiModel>>
}
