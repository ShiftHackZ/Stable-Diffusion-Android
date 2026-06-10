package com.shifthackz.aisdv1.data.core

import com.shifthackz.aisdv1.data.export.toMediaStoreImageBytes
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.MediaStoreInfo
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal abstract class CoreMediaStoreRepository(
    private val preferenceManager: PreferenceManager,
    private val mediaStoreGateway: MediaStoreGateway,
) {

    protected suspend fun exportToMediaStoreAsync(result: AiGenerationResult) {
        if (preferenceManager.saveToMediaStore) exportAsync(result)
    }

    protected suspend fun getInfoAsync(): MediaStoreInfo = mediaStoreGateway.getInfo()

    private fun exportAsync(result: AiGenerationResult) {
        runCatching {
            mediaStoreGateway.exportToFile(
                fileName = "sdai_${Clock.System.now().toEpochMilliseconds()}",
                content = result.image.toMediaStoreImageBytes(),
            )
        }
    }
}
