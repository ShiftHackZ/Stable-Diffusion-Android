package com.shifthackz.aisdv1.feature.onnx.extensions

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.feature.onnx.environment.LocalModelIdProvider

fun modelPathPrefix(
    preferenceManager: PreferenceManager,
    fileProviderDescriptor: FileProviderDescriptor,
    localModelIdProvider: LocalModelIdProvider,
): String {
    val modelId = localModelIdProvider.get()
    return if (modelId == LocalAiModel.CustomOnnx.id) {
        preferenceManager.localOnnxCustomModelPath
    } else {
        "${fileProviderDescriptor.localModelDirPath}/${modelId}"
    }
}
