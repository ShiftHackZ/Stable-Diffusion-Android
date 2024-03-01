package com.shifthackz.aisdv1.presentation.screen.setup.steps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupIntent
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupState
import com.shifthackz.aisdv1.presentation.screen.setup.forms.Automatic1111Form
import com.shifthackz.aisdv1.presentation.screen.setup.forms.HordeForm
import com.shifthackz.aisdv1.presentation.screen.setup.forms.HuggingFaceForm
import com.shifthackz.aisdv1.presentation.screen.setup.forms.LocalDiffusionForm
import com.shifthackz.aisdv1.presentation.screen.setup.forms.OpenAiForm

@Composable
fun ConfigurationStep(
    modifier: Modifier = Modifier,
    state: ServerSetupState,
    buildInfoProvider: BuildInfoProvider = BuildInfoProvider.stub,
    handleIntent: (ServerSetupIntent) -> Unit = {},
) {
    BaseServerSetupStateWrapper(modifier) {
        when (state.mode) {
            ServerSource.AUTOMATIC1111 -> Automatic1111Form(
                state = state,
                handleIntent = handleIntent,
            )

            ServerSource.HORDE -> HordeForm(
                state = state,
                handleIntent = handleIntent,
            )

            ServerSource.LOCAL -> LocalDiffusionForm(
                state = state,
                buildInfoProvider = buildInfoProvider,
                handleIntent = handleIntent,
            )

            ServerSource.HUGGING_FACE -> HuggingFaceForm(
                state = state,
                handleIntent = handleIntent,
            )

            ServerSource.OPEN_AI -> OpenAiForm(
                state = state,
                handleIntent = handleIntent,
            )
        }
    }
}
