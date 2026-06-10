package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapRawToCheckpointDomain
import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.network.api.sdai.SdaiAppApi

internal class DownloadableModelRemoteDataSource(
    private val api: SdaiAppApi,
    private val fileDownloader: DownloadableModelFileDownloader,
) : DownloadableModelDataSource.Remote {

    override suspend fun fetch(): List<LocalAiModel> {
        val onnx = api
            .fetchOnnxModels()
            .mapRawToCheckpointDomain(LocalAiModel.Type.ONNX)
        val mediaPipe = api
            .fetchMediaPipeModels()
            .mapRawToCheckpointDomain(LocalAiModel.Type.MediaPipe)
        return onnx + mediaPipe
    }

    override fun download(id: String, url: String) = fileDownloader.download(id, url)
}
