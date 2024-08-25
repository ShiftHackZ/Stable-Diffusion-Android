package com.shifthackz.aisdv1.domain.interactor.settings

import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToA1111UseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToHordeUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToHuggingFaceUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToLocalDiffusionUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToMediaPipeUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToOpenAiUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToStabilityAiUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToSwarmUiUseCase

internal data class SetupConnectionInterActorImpl(
    override val connectToHorde: ConnectToHordeUseCase,
    override val connectToLocal: ConnectToLocalDiffusionUseCase,
    override val connectToMediaPipe: ConnectToMediaPipeUseCase,
    override val connectToA1111: ConnectToA1111UseCase,
    override val connectToHuggingFace: ConnectToHuggingFaceUseCase,
    override val connectToOpenAi: ConnectToOpenAiUseCase,
    override val connectToStabilityAi: ConnectToStabilityAiUseCase,
    override val connectToSwarmUi: ConnectToSwarmUiUseCase,
) : SetupConnectionInterActor
