package com.shifthackz.aisdv1.presentation.core

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class GenerationFormUpdateEvent {

    private val sRoute = MutableSharedFlow<AiGenerationResult.Type>(
        extraBufferCapacity = 64,
    )
    private val sTxt2Img = MutableStateFlow<Payload>(Payload.None)
    private val sImg2Img = MutableStateFlow<Payload>(Payload.None)

    fun update(
        generation: AiGenerationResult,
        route: AiGenerationResult.Type,
        inputImage: Boolean,
    ) {
        sRoute.tryEmit(route)
        when (route) {
            AiGenerationResult.Type.TEXT_TO_IMAGE -> sTxt2Img.value = Payload.T2IForm(generation)
            AiGenerationResult.Type.IMAGE_TO_IMAGE -> sImg2Img.value = Payload.I2IForm(generation, inputImage)
        }
    }

    fun clear() {
        sTxt2Img.value = Payload.None
        sImg2Img.value = Payload.None
    }

    fun clearTxt2Img() {
        sTxt2Img.value = Payload.None
    }

    fun clearImg2Img() {
        sImg2Img.value = Payload.None
    }

    fun consumeTxt2ImgForm(): Payload.T2IForm? =
        (sTxt2Img.value as? Payload.T2IForm)?.also { clearTxt2Img() }

    fun consumeImg2ImgForm(): Payload.I2IForm? =
        (sImg2Img.value as? Payload.I2IForm)?.also { clearImg2Img() }

    fun observeRoute(): Flow<AiGenerationResult.Type> = sRoute.asSharedFlow()

    fun observeTxt2ImgForm(): Flow<Payload> = sTxt2Img.asStateFlow()

    fun observeImg2ImgForm(): Flow<Payload> = sImg2Img.asStateFlow()

    sealed interface Payload {
        data object None : Payload
        data class T2IForm(val ai: AiGenerationResult) : Payload
        data class I2IForm(val ai: AiGenerationResult, val inputImage: Boolean) : Payload
    }
}
