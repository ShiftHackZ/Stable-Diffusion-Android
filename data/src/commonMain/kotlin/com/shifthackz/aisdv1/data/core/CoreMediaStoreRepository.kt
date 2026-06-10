package com.shifthackz.aisdv1.data.core

import com.shifthackz.aisdv1.data.export.toMediaStoreImageBytes
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.MediaStoreInfo
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Coordinates `CoreMediaStoreRepository` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalTime::class)
internal abstract class CoreMediaStoreRepository(
    /**
     * Exposes the `preferenceManager` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
    /**
     * Exposes the `mediaStoreGateway` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val mediaStoreGateway: MediaStoreGateway,
) {

    /**
     * Executes the `exportToMediaStoreAsync` step in the SDAI data layer.
     *
     * @param result result value consumed by the API.
     * @author Dmitriy Moroz
     */
    protected suspend fun exportToMediaStoreAsync(result: AiGenerationResult) {
        if (preferenceManager.saveToMediaStore) exportAsync(result)
    }

    /**
     * Loads SDAI data through `getInfoAsync`.
     *
     * @author Dmitriy Moroz
     */
    protected suspend fun getInfoAsync(): MediaStoreInfo = mediaStoreGateway.getInfo()

    /**
     * Executes the `exportAsync` step in the SDAI data layer.
     *
     * @param result result value consumed by the API.
     * @author Dmitriy Moroz
     */
    private fun exportAsync(result: AiGenerationResult) {
        runCatching {
            mediaStoreGateway.exportToFile(
                fileName = "sdai_${Clock.System.now().toEpochMilliseconds()}",
                content = result.image.toMediaStoreImageBytes(),
            )
        }
    }
}
