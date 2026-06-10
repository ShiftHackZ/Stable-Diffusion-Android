package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.core.imageprocessing.BitmapToBase64Converter
import com.shifthackz.aisdv1.data.core.CoreGenerationRepository
import com.shifthackz.aisdv1.data.mappers.mapLocalDiffusionToAiGenResult
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.mediapipe.MediaPipe
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.MediaPipeGenerationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class MediaPipeGenerationRepositoryImpl(
    mediaStoreGateway: MediaStoreGateway,
    localDataSource: GenerationResultDataSource.Local,
    backgroundWorkObserver: BackgroundWorkObserver,
    preferenceManager: PreferenceManager,
    private val mediaPipe: MediaPipe,
    private val bitmapToBase64Converter: BitmapToBase64Converter,
) : CoreGenerationRepository(
    mediaStoreGateway = mediaStoreGateway,
    localDataSource = localDataSource,
    preferenceManager = preferenceManager,
    backgroundWorkObserver = backgroundWorkObserver,
), MediaPipeGenerationRepository {

    override suspend fun generateFromText(payload: TextToImagePayload): AiGenerationResult = withContext(Dispatchers.Default) {
        mediaPipe
            .process(payload)
            .let(BitmapToBase64Converter::Input)
            .let(bitmapToBase64Converter::invoke)
            .base64ImageString
            .let { base64 -> payload to base64 }
            .mapLocalDiffusionToAiGenResult()
    }
        .let { result -> insertGenerationResult(result) }
}
