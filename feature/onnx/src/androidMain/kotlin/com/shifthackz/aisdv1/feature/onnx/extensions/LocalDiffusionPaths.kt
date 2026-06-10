package com.shifthackz.aisdv1.feature.onnx.extensions

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.feature.onnx.environment.LocalModelIdProvider

/**
 * Executes the `modelPathPrefix` step in the SDAI ONNX local diffusion feature layer.
 *
 * @param preferenceManager preference manager value consumed by the API.
 * @param fileProviderDescriptor file provider descriptor value consumed by the API.
 * @param localModelIdProvider local model id provider value consumed by the API.
 * @return Result produced by `modelPathPrefix`.
 * @author Dmitriy Moroz
 */
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
