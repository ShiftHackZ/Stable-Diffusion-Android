@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.presentation.widget.DropdownTextField

class SettingsScreen(
    private val viewModel: SettingsViewModel,
) : MviScreen<SettingsState, EmptyEffect>(viewModel) {

    @Composable
    override fun Content() {
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = viewModel.state.collectAsState().value,
            onSdModelSelected = viewModel::selectStableDiffusionModel,
        )
    }

    @Composable
    override fun ApplySystemUiColors() = Unit
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: SettingsState,
    onSdModelSelected: (String) -> Unit = {},
) {
    Box(modifier) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
//                        Text(stringResource(id = R.string.title_text_to_image))
                        Text("Settings")
                    },
                )
            },
            content = { paddingValues ->
                val contentModifier = Modifier.padding(paddingValues)
                when (state) {
                    SettingsState.Uninitialized -> Text("Load")
                    is SettingsState.Content -> ContentSettingsState(
                        modifier = contentModifier.padding(horizontal = 16.dp),
                        state = state,
                        onSdModelSelected = onSdModelSelected,
                    )
                }
            }
        )
    }
}

@Composable
private fun ContentSettingsState(
    modifier: Modifier = Modifier,
    state: SettingsState.Content,
    onSdModelSelected: (String) -> Unit = {},
) {
    Column(modifier) {
        DropdownTextField(
            modifier = Modifier.fillMaxWidth(),
            label = "Selected model".asUiText(),
            value = state.sdModelSelected,
            items = state.sdModels,
            onItemSelected = onSdModelSelected,
        )
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
private fun PreviewStateContent() {
    ScreenContent(
        modifier = Modifier.fillMaxSize(),
        state = SettingsState.Content(
            sdModels = listOf("Stable diffusion v1.5"),
            sdModelSelected = "Stable diffusion v1.5"
        )
    )
}
