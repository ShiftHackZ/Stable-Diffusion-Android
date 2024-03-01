@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.debug

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SettingsEthernet
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.MviComposable
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun DebugMenuScreen() {
    MviComposable(
        viewModel = koinViewModel<DebugMenuViewModel>(),
    ) { _, intentHandler ->
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            handleIntent = intentHandler,
        )
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    handleIntent: (DebugMenuIntent) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.title_debug_menu),
                        style = MaterialTheme.typography.headlineMedium,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { handleIntent(DebugMenuIntent.NavigateBack) },
                        content = {
                            Icon(
                                Icons.Outlined.ArrowBack,
                                contentDescription = "Back button",
                            )
                        },
                    )
                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
        ) {
            val headerModifier = Modifier.padding(vertical = 16.dp)
            val itemModifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)

            Text(
                modifier = headerModifier,
                text = stringResource(id = R.string.debug_section_qa),
                style = MaterialTheme.typography.headlineSmall,
            )
            SettingsItem(
                modifier = itemModifier,
                startIcon = Icons.Default.SettingsEthernet,
                text = R.string.debug_action_bad_base64.asUiText(),
                onClick = { handleIntent(DebugMenuIntent.InsertBadBase64) },
            )
        }
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
private fun DebugMenuScreenPreview() {
    ScreenContent()
}
