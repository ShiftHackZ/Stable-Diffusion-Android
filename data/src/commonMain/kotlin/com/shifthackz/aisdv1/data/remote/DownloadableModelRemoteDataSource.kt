package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapRawToCheckpointDomain
import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.network.api.sdai.SdaiAppApi

/**
 * Coordinates `DownloadableModelRemoteDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class DownloadableModelRemoteDataSource(
    /**
     * Exposes the `api` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val api: SdaiAppApi,
    /**
     * Exposes the `fileDownloader` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val fileDownloader: DownloadableModelFileDownloader,
) : DownloadableModelDataSource.Remote {

    /**
     * Loads SDAI data through `fetch`.
     *
     * @return Result produced by `fetch`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetch(): List<LocalAiModel> {
        val onnx = api
            .fetchOnnxModels()
            .mapRawToCheckpointDomain(LocalAiModel.Type.ONNX)
        val mediaPipe = api
            .fetchMediaPipeModels()
            .mapRawToCheckpointDomain(LocalAiModel.Type.MediaPipe)
        val sdxl = runCatching {
            api
                .fetchSdxlModels()
                .mapRawToCheckpointDomain(LocalAiModel.Type.Sdxl)
        }.getOrElse { emptyList() }
        val coreMl = runCatching {
            api
                .fetchCoreMlModels()
                .mapRawToCheckpointDomain(LocalAiModel.Type.CoreMl)
        }.getOrElse { emptyList() }
        return onnx + mediaPipe + sdxl + coreMl
    }

    /**
     * Executes the `download` step in the SDAI data layer.
     *
     * @param id identifier of the target entity.
     * @param url remote URL used by the operation.
     * @author Dmitriy Moroz
     */
    override fun download(id: String, url: String) = fileDownloader.download(id, url)
}
