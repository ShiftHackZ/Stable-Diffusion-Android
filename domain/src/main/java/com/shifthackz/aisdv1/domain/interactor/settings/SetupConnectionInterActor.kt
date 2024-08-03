package com.shifthackz.aisdv1.domain.interactor.settings

import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToA1111UseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToHordeUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToHuggingFaceUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToLocalDiffusionUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToOpenAiUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToStabilityAiUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToSwarmUiUseCase

interface SetupConnectionInterActor {
    val connectToHorde: ConnectToHordeUseCase
    val connectToLocal: ConnectToLocalDiffusionUseCase
    val connectToA1111: ConnectToA1111UseCase
    val connectToHuggingFace: ConnectToHuggingFaceUseCase
    val connectToOpenAi: ConnectToOpenAiUseCase
    val connectToStabilityAi: ConnectToStabilityAiUseCase
    val connectToSwarmUi: ConnectToSwarmUiUseCase
}
