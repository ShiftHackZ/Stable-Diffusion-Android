package com.shifthackz.aisdv1.presentation.widget.version

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.domain.usecase.version.CheckAppVersionUpdateUseCase
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.widget.dialog.DecisionInteractiveDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ForceUpdateDialog

class VersionCheckerComposable(
    private val viewModel: VersionCheckerViewModel,
    private val launchMarket: () -> Unit,
) : MviScreen<VersionCheckerState, EmptyEffect>(viewModel) {

    @Composable
    override fun Content() {
        val state = viewModel.state.collectAsState().value
        if (state == VersionCheckerState.Idle) return
        Box(Modifier.fillMaxSize()) {
            (state as VersionCheckerState.UpdatePopUp)
            when (state.result) {
                is CheckAppVersionUpdateUseCase.Result.NewVersionAvailable -> {
                    if (!state.forceUserToUpdate) DecisionInteractiveDialog(
                        title = R.string.update_title.asUiText(),
                        text = UiText.Resource(
                            R.string.update_sub_title,
                            "${state.result.availableVersion}",
                        ),
                        confirmActionResId = R.string.action_update,
                        dismissActionResId = R.string.action_skip,
                        onConfirmAction = {
                            launchMarket()
                            viewModel.skipUpdate()
                        },
                        onDismissRequest = viewModel::skipUpdate,
                    )
                    else ForceUpdateDialog(openMarket = launchMarket)
                }
                CheckAppVersionUpdateUseCase.Result.NoUpdateNeeded -> {
                    AlertDialog(
                        shape = RoundedCornerShape(24.dp),
                        onDismissRequest = viewModel::skipUpdate,
                        confirmButton = {
                            TextButton(onClick = viewModel::skipUpdate) {
                                Text(text = stringResource(id = R.string.ok))
                            }
                        },
                        title = {
                            Text(
                                text = stringResource(id = R.string.update_no_need_title),
                                fontSize = 18.sp,
                                color = AlertDialogDefaults.titleContentColor,
                            )
                        },
                        text = {
                            Text(
                                text = stringResource(id = R.string.update_no_need_sub_title),
                                fontSize = 14.sp,
                                color = AlertDialogDefaults.textContentColor,
                            )
                        }
                    )
                }
            }
        }
    }

    @Composable
    override fun ApplySystemUiColors() = Unit
}
