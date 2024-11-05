package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.ReportDataSource
import com.shifthackz.aisdv1.domain.entity.ReportReason
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.ReportRepository
import io.reactivex.rxjava3.core.Completable

internal class ReportRepositoryImpl(
    private val rds: ReportDataSource.Remote,
    private val preferenceManager: PreferenceManager,
) : ReportRepository {

    override fun send(text: String, reason: ReportReason, image: String): Completable {
        val source = preferenceManager.source
        val model = when (source) {
            ServerSource.HUGGING_FACE -> preferenceManager.huggingFaceModel
            ServerSource.STABILITY_AI -> preferenceManager.stabilityAiEngineId
            ServerSource.LOCAL_MICROSOFT_ONNX -> preferenceManager.localOnnxModelId
            ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> preferenceManager.localMediaPipeModelId
            else -> ""
        }
        return rds.send(text, reason, image, source.toString(), model)
    }
}
