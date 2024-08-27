package com.shifthackz.aisdv1.presentation.mocks

import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupState

val mockLocalAiModels = listOf(
    LocalAiModel.CustomOnnx,
    LocalAiModel(
        id = "1",
        type = LocalAiModel.Type.ONNX,
        name = "Model 1",
        size = "5 Gb",
        sources = listOf("https://example.com/1.html"),
        downloaded = false,
        selected = false,
    ),
)

val mockServerSetupStateLocalModel = ServerSetupState.LocalModel(
    id = "1",
    name = "Model 1",
    size = "5 Gb",
    downloaded = false,
    downloadState = DownloadState.Unknown,
    selected = false,
)
