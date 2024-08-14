package com.shifthackz.aisdv1.feature.diffusion.extensions

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.feature.diffusion.environment.LocalModelIdProvider

fun modelPathPrefix(
    preferenceManager: PreferenceManager,
    fileProviderDescriptor: FileProviderDescriptor,
    localModelIdProvider: LocalModelIdProvider,
): String {
    val modelId = localModelIdProvider.get()
    return if (modelId == LocalAiModel.CUSTOM.id) {
        preferenceManager.localDiffusionCustomModelPath
    } else {
        "${fileProviderDescriptor.localModelDirPath}/${modelId}"
    }
}
