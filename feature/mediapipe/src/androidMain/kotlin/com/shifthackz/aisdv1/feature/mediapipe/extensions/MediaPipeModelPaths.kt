package com.shifthackz.aisdv1.feature.mediapipe.extensions

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.preference.PreferenceManager

/**
 * Executes the `modelPath` step in the SDAI MediaPipe feature layer.
 *
 * @param preferenceManager preference manager value consumed by the API.
 * @param fileProviderDescriptor file provider descriptor value consumed by the API.
 * @return Result produced by `modelPath`.
 * @author Dmitriy Moroz
 */
fun modelPath(
    preferenceManager: PreferenceManager,
    fileProviderDescriptor: FileProviderDescriptor,
): String {
    val modelId = preferenceManager.localMediaPipeModelId
    return if (modelId == LocalAiModel.CustomMediaPipe.id) {
        preferenceManager.localMediaPipeCustomModelPath
    } else {
        "${fileProviderDescriptor.localModelDirPath}/${modelId}"
    }
}
