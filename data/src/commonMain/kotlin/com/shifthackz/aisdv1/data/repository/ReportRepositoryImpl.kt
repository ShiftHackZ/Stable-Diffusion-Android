package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.ReportDataSource
import com.shifthackz.aisdv1.domain.entity.ReportReason
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.ReportRepository

/**
 * Implements `ReportRepository` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class ReportRepositoryImpl(
    /**
     * Exposes the `rds` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val rds: ReportDataSource.Remote,
    /**
     * Exposes the `preferenceManager` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
) : ReportRepository {

    /**
     * Executes the `send` step in the SDAI data layer.
     *
     * @param text text value consumed by the API.
     * @param reason reason value consumed by the API.
     * @param image image value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun send(text: String, reason: ReportReason, image: String) {
        val source = preferenceManager.source
        val model = when (source) {
            ServerSource.HUGGING_FACE -> preferenceManager.huggingFaceModel
            ServerSource.STABILITY_AI -> preferenceManager.stabilityAiEngineId
            ServerSource.LOCAL_MICROSOFT_ONNX -> preferenceManager.localOnnxModelId
            ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> preferenceManager.localMediaPipeModelId
            else -> ""
        }
        rds.send(text, reason, image, source.toString(), model)
    }
}
