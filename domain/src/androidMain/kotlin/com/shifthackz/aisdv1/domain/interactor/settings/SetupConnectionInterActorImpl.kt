package com.shifthackz.aisdv1.domain.interactor.settings

import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToA1111UseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToHordeUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToHuggingFaceUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToLocalDiffusionUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToMediaPipeUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToOpenAiUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToStabilityAiUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToSwarmUiUseCase

/**
 * Implements `SetupConnectionInterActor` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal data class SetupConnectionInterActorImpl(
    /**
     * Exposes the `connectToHorde` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override val connectToHorde: ConnectToHordeUseCase,
    /**
     * Exposes the `connectToLocal` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override val connectToLocal: ConnectToLocalDiffusionUseCase,
    /**
     * Exposes the `connectToMediaPipe` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override val connectToMediaPipe: ConnectToMediaPipeUseCase,
    /**
     * Exposes the `connectToA1111` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override val connectToA1111: ConnectToA1111UseCase,
    /**
     * Exposes the `connectToHuggingFace` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override val connectToHuggingFace: ConnectToHuggingFaceUseCase,
    /**
     * Exposes the `connectToOpenAi` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override val connectToOpenAi: ConnectToOpenAiUseCase,
    /**
     * Exposes the `connectToStabilityAi` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override val connectToStabilityAi: ConnectToStabilityAiUseCase,
    /**
     * Exposes the `connectToSwarmUi` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override val connectToSwarmUi: ConnectToSwarmUiUseCase,
) : SetupConnectionInterActor
