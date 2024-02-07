package com.shifthackz.aisdv1.feature.diffusion.extensions

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.feature.diffusion.environment.LocalModelIdProvider

private const val PATH = "/storage/emulated/0/Download/SDAI/model"

fun modelPathPrefix(
    fileProviderDescriptor: FileProviderDescriptor,
    localModelIdProvider: LocalModelIdProvider,
): String {
    val modelId = localModelIdProvider.get();
    return if (modelId == LocalAiModel.CUSTOM.id) {
        PATH
    } else {
        "${fileProviderDescriptor.localModelDirPath}/${modelId}"
    }
}
