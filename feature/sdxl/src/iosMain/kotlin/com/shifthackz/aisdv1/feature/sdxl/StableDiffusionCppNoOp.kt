package com.shifthackz.aisdv1.feature.sdxl

import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.sdxl.StableDiffusionCpp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal object StableDiffusionCppNoOp : StableDiffusionCpp {

    override suspend fun process(
        payload: TextToImagePayload,
        modelPath: String,
    ): String = error("Local SDXL stable-diffusion.cpp generation is unavailable on this device.")

    override suspend fun interrupt() = Unit

    override fun observeStatus(): Flow<LocalDiffusionStatus> =
        flowOf(LocalDiffusionStatus(current = 0, total = 0))
}
