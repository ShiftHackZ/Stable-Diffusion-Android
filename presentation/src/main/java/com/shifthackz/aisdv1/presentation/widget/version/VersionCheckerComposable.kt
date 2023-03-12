package com.shifthackz.aisdv1.presentation.widget.version

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.widget.dialog.DecisionInteractiveDialog

class VersionCheckerComposable(
    private val viewModel: VersionCheckerViewModel,
) : MviScreen<VersionCheckerState, EmptyEffect>(viewModel) {

    @Composable
    override fun Content() {
        val state = viewModel.state.collectAsState().value
        if (state == VersionCheckerState.Idle) return
        val newVersion = (state as VersionCheckerState.UpdatePopUp).result.availableVersion
        Box(Modifier.fillMaxSize()) {
            DecisionInteractiveDialog(
                title = R.string.update_title.asUiText(),
                text = UiText.Resource(R.string.update_sub_title, "$newVersion"),
                confirmActionResId = R.string.action_update,
                dismissActionResId = R.string.action_skip,
                onConfirmAction = {
                    println("GO TO THE UPDATE")
                    viewModel.skipUpdate()
                },
                onDismissRequest = viewModel::skipUpdate,
            )
        }
    }

    @Composable
    override fun ApplySystemUiColors() = Unit
}
