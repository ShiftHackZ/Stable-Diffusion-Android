package com.shifthackz.aisdv1.presentation.widget.engine

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.MviComponent
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.widget.input.DropdownTextField
import org.koin.androidx.compose.koinViewModel
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun EngineSelectionComponent(
    modifier: Modifier = Modifier,
) {
    MviComponent(
        viewModel = koinViewModel<EngineSelectionViewModel>(),
        applySystemUiColors = false,
    ) { state, intentHandler ->
        when (state.mode) {
            ServerSource.AUTOMATIC1111 -> DropdownTextField(
                label = LocalizationR.string.hint_sd_model.asUiText(),
                loading = state.loading,
                modifier = modifier,
                value = state.selectedSdModel,
                items = state.sdModels,
                onItemSelected = { intentHandler(EngineSelectionIntent(it)) },
            )

            ServerSource.SWARM_UI -> DropdownTextField(
                label = LocalizationR.string.hint_sd_model.asUiText(),
                loading = state.loading,
                modifier = modifier,
                value = state.selectedSwarmModel,
                items = state.swarmModels,
                onItemSelected = { intentHandler(EngineSelectionIntent(it)) },
            )

            ServerSource.HUGGING_FACE -> DropdownTextField(
                label = LocalizationR.string.hint_hugging_face_model.asUiText(),
                loading = state.loading,
                modifier = modifier,
                value = state.selectedHfModel,
                items = state.hfModels,
                onItemSelected = { intentHandler(EngineSelectionIntent(it)) },
            )

            ServerSource.STABILITY_AI -> DropdownTextField(
                label = LocalizationR.string.hint_stability_ai_engine.asUiText(),
                loading = state.loading,
                modifier = modifier,
                value = state.selectedStEngine,
                items = state.stEngines,
                onItemSelected = { intentHandler(EngineSelectionIntent(it)) },
            )

            ServerSource.LOCAL -> DropdownTextField(
                label = LocalizationR.string.hint_sd_model.asUiText(),
                loading = state.loading,
                modifier = modifier,
                value = state.localAiModels.firstOrNull { it.id == state.selectedLocalAiModelId },
                items = state.localAiModels,
                onItemSelected = { intentHandler(EngineSelectionIntent(it.id)) },
                displayDelegate = { it.name.asUiText() },
            )

            ServerSource.HORDE -> Unit
            ServerSource.OPEN_AI -> Unit
        }
    }
}
