@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.debug

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SettingsEthernet
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.ui.EmptyState
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem

class DebugMenuScreen(
    private val viewModel: DebugMenuViewModel,
) : MviScreen<EmptyState, EmptyEffect>(viewModel) {

    @Composable
    override fun Content() {
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            onInsertBadBase64 = viewModel::insertBadBase64,
        )
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    onInsertBadBase64: () -> Unit = {},
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
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            val headerModifier = Modifier.padding(vertical = 16.dp)
            val itemModifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)

            Text(
                modifier = headerModifier,
                text = "Stub actions",
                style = MaterialTheme.typography.headlineSmall,
            )
            SettingsItem(
                modifier = itemModifier,
                startIcon = Icons.Default.SettingsEthernet,
                text = "Insert bad base64".asUiText(),
                onClick = onInsertBadBase64,
            )
        }
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
private fun DebugMenuScreenPreview() {
    ScreenContent()
}
