package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import kotlinx.coroutines.flow.Flow

sealed interface DownloadableModelDataSource {

    interface Remote : DownloadableModelDataSource {
        suspend fun fetch(): List<LocalAiModel>
        fun download(id: String, url: String): Flow<DownloadState>
    }

    interface Local : DownloadableModelDataSource {
        suspend fun getAllOnnx(): List<LocalAiModel>
        suspend fun getAllMediaPipe(): List<LocalAiModel>
        suspend fun getById(id: String): LocalAiModel
        suspend fun getSelectedOnnx(): LocalAiModel
        fun observeAllOnnx(): Flow<List<LocalAiModel>>
        suspend fun save(list: List<LocalAiModel>)
        suspend fun delete(id: String)
    }
}
