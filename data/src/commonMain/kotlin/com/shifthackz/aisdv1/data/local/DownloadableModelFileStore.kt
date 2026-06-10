package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.domain.entity.LocalAiModel

internal interface DownloadableModelFileStore {
    fun isDownloaded(model: LocalAiModel): Boolean
    fun delete(id: String)
}

internal object NoOpDownloadableModelFileStore : DownloadableModelFileStore {

    override fun isDownloaded(model: LocalAiModel): Boolean =
        model.id == LocalAiModel.CustomOnnx.id || model.id == LocalAiModel.CustomMediaPipe.id

    override fun delete(id: String) = Unit
}
