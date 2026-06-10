package com.shifthackz.aisdv1.presentation.core

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Coordinates `GenerationFormUpdateEvent` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
class GenerationFormUpdateEvent {

    /**
     * Exposes the `sRoute` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val sRoute = MutableSharedFlow<AiGenerationResult.Type>(
        extraBufferCapacity = 64,
    )
    /**
     * Exposes the `sTxt2Img` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val sTxt2Img = MutableStateFlow<Payload>(Payload.None)
    /**
     * Exposes the `sImg2Img` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val sImg2Img = MutableStateFlow<Payload>(Payload.None)

    /**
     * Performs the SDAI side effect handled by `update`.
     *
     * @param generation generation value consumed by the API.
     * @param route route value consumed by the API.
     * @param inputImage input image value consumed by the API.
     * @author Dmitriy Moroz
     */
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

    /**
     * Performs the SDAI side effect handled by `clear`.
     *
     * @author Dmitriy Moroz
     */
    fun clear() {
        sTxt2Img.value = Payload.None
        sImg2Img.value = Payload.None
    }

    /**
     * Performs the SDAI side effect handled by `clearTxt2Img`.
     *
     * @author Dmitriy Moroz
     */
    fun clearTxt2Img() {
        sTxt2Img.value = Payload.None
    }

    /**
     * Performs the SDAI side effect handled by `clearImg2Img`.
     *
     * @author Dmitriy Moroz
     */
    fun clearImg2Img() {
        sImg2Img.value = Payload.None
    }

    /**
     * Executes the `consumeTxt2ImgForm` step in the SDAI presentation layer.
     *
     * @return Result produced by `consumeTxt2ImgForm`.
     * @author Dmitriy Moroz
     */
    fun consumeTxt2ImgForm(): Payload.T2IForm? =
        (sTxt2Img.value as? Payload.T2IForm)?.also { clearTxt2Img() }

    /**
     * Executes the `consumeImg2ImgForm` step in the SDAI presentation layer.
     *
     * @return Result produced by `consumeImg2ImgForm`.
     * @author Dmitriy Moroz
     */
    fun consumeImg2ImgForm(): Payload.I2IForm? =
        (sImg2Img.value as? Payload.I2IForm)?.also { clearImg2Img() }

    /**
     * Loads SDAI data through `observeRoute`.
     *
     * @author Dmitriy Moroz
     */
    fun observeRoute(): Flow<AiGenerationResult.Type> = sRoute.asSharedFlow()

    /**
     * Loads SDAI data through `observeTxt2ImgForm`.
     *
     * @author Dmitriy Moroz
     */
    fun observeTxt2ImgForm(): Flow<Payload> = sTxt2Img.asStateFlow()

    /**
     * Loads SDAI data through `observeImg2ImgForm`.
     *
     * @author Dmitriy Moroz
     */
    fun observeImg2ImgForm(): Flow<Payload> = sImg2Img.asStateFlow()

    /**
     * Defines the `Payload` contract for the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    sealed interface Payload {
        /**
         * Provides the `None` singleton used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data object None : Payload
        /**
         * Carries `T2IForm` data through the SDAI presentation layer.
         *
         * @param ai ai value consumed by the API.
         * @author Dmitriy Moroz
         */
        data class T2IForm(val ai: AiGenerationResult) : Payload
        /**
         * Carries `I2IForm` data through the SDAI presentation layer.
         *
         * @param ai ai value consumed by the API.
         * @param inputImage input image value consumed by the API.
         * @author Dmitriy Moroz
         */
        data class I2IForm(val ai: AiGenerationResult, val inputImage: Boolean) : Payload
    }
}
