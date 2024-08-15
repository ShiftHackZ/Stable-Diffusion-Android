@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.backup

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import com.shifthackz.aisdv1.core.localization.R
import com.shifthackz.aisdv1.core.ui.MviComponent
import org.koin.androidx.compose.koinViewModel

@Composable
fun BackupScreen() {
    MviComponent(
        viewModel = koinViewModel<BackupViewModel>(),
    ) { state, processIntent ->
        BackupScreenContent(
            state = state,
            processIntent = processIntent,
        )
    }
}

@Composable
private fun BackupScreenContent(
    modifier: Modifier = Modifier,
    state: BackupState,
    processIntent: (BackupIntent) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.title_backup),
                        style = MaterialTheme.typography.headlineMedium,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { processIntent(BackupIntent.NavigateBack) },
                        content = {
                            Icon(
                                Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Back button",
                            )
                        },
                    )
                },
            )
        }
    ) { paddingValues ->

    }
}
