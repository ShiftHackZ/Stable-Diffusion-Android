package com.shifthackz.aisdv1.presentation.screen.setup.forms

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupIntent
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupState

@Composable
fun MediaPipeForm(
    modifier: Modifier = Modifier,
    state: ServerSetupState,
    buildInfoProvider: BuildInfoProvider = BuildInfoProvider.stub,
    processIntent: (ServerSetupIntent) -> Unit = {},
) {
    when (buildInfoProvider.type) {
        BuildType.FOSS -> {

        }

        else -> LocalDiffusionForm(
            modifier = modifier,
            state = state,
            buildInfoProvider = buildInfoProvider,
            processIntent = processIntent,
        )
    }
}
