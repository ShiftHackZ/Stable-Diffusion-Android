@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.ui.MviScreen

class SettingsScreen(
    private val viewModel: SettingsViewModel,
) : MviScreen<SettingsState, EmptyEffect>(viewModel) {

    @Composable
    override fun Content() {
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = viewModel.state.collectAsState().value,
        )
    }

    @Composable
    override fun ApplySystemUiColors() = Unit
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: SettingsState,
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
                Column(Modifier.padding(paddingValues)) {

                }
            }
        )
    }
}
/*
        val models: List<String>,
        val selectedModel: UiText = UiText.empty,
 */

//        var sdModelsExpanded by remember { mutableStateOf(false) }
//        ExposedDropdownMenuBox(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 8.dp),
//            expanded = sdModelsExpanded,
//            onExpandedChange = { sdModelsExpanded = !sdModelsExpanded },
//        ) {
//            val selectedModel = state.selectedModel.asString()
//            TextField(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .menuAnchor(),
//                value = selectedModel,
//                onValueChange = {},
//                readOnly = true,
//                label = { Text("SD Model") },
//                trailingIcon = {
//                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = sdModelsExpanded)
//                }
//            )
//
//            ExposedDropdownMenu(
//                expanded = sdModelsExpanded,
//                onDismissRequest = { sdModelsExpanded = false },
//            ) {
//                state.models.forEach { title ->
//                    DropdownMenuItem(
//                        text = { Text(title) },
//                        onClick = {
//                            sdModelsExpanded = false
//                            if (selectedModel == title) return@DropdownMenuItem
//                            onSelectedSdModel(title)
//                        },
//                    )
//                }
//            }
//        }