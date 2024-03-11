package com.shifthackz.aisdv1.presentation.widget.engine

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.MviComponent
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.widget.input.DropdownTextField
import org.koin.androidx.compose.koinViewModel

@Composable
fun EngineSelectionComponent(
    modifier: Modifier = Modifier,
) {
    MviComponent(
        viewModel = koinViewModel<EngineSelectionViewModel>(),
    ) { state, intentHandler ->
        when (state.mode) {
            ServerSource.AUTOMATIC1111 -> DropdownTextField(
                label = R.string.hint_sd_model.asUiText(),
                loading = state.loading,
                modifier = modifier,
                value = state.selectedSdModel,
                items = state.sdModels,
                onItemSelected = { intentHandler(EngineSelectionIntent(it)) },
            )

            ServerSource.HUGGING_FACE -> DropdownTextField(
                label = R.string.hint_hugging_face_model.asUiText(),
                loading = state.loading,
                modifier = modifier,
                value = state.selectedHfModel,
                items = state.hfModels,
                onItemSelected = { intentHandler(EngineSelectionIntent(it)) },
            )

            ServerSource.STABILITY_AI -> DropdownTextField(
                label = R.string.hint_stability_ai_engine.asUiText(),
                loading = state.loading,
                modifier = modifier,
                value = state.selectedStEngine,
                items = state.stEngines,
                onItemSelected = { intentHandler(EngineSelectionIntent(it)) },
            )

            else -> Unit
        }
    }
}
