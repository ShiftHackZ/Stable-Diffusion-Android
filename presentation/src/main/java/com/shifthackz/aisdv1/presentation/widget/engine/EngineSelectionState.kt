package com.shifthackz.aisdv1.presentation.widget.engine

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.android.core.mvi.MviState

@Immutable
data class EngineSelectionState(
    val loading: Boolean = true,
    val mode: ServerSource = ServerSource.AUTOMATIC1111,
    val sdModels: List<String> = emptyList(),
    val selectedSdModel: String = "",
    val swarmModels: List<String> = emptyList(),
    val selectedSwarmModel: String = "",
    val hfModels: List<String> = emptyList(),
    val selectedHfModel: String = "",
    val stEngines: List<String> = emptyList(),
    val selectedStEngine: String = "",
    val localAiModels: List<LocalAiModel> = emptyList(),
    val selectedLocalAiModelId: String = "",
): MviState
