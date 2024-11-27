package com.shifthackz.aisdv1.presentation.screen.backup.forms

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.common.file.LOCAL_DIFFUSION_CUSTOM_PATH
import com.shifthackz.aisdv1.core.localization.R
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.presentation.screen.backup.BackupIntent
import com.shifthackz.aisdv1.presentation.screen.backup.BackupState
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupIntent
import com.shifthackz.aisdv1.presentation.theme.textFieldColors
import com.shifthackz.aisdv1.presentation.utils.readByteArrayFromUri

@Composable
fun RestoreBackupForm(
    state: BackupState,
    processIntent: (BackupIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri ?: return@rememberLauncherForActivityResult
            readByteArrayFromUri(context, uri)?.let {
                processIntent(BackupIntent.SelectRestore("${uri.path}", it))
            }
        }
    )

    Column(
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 8.dp),
            text = stringResource(id = R.string.model_local_path_header),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
            value = state.backupToRestore?.first ?: "",
            onValueChange = {},
            readOnly = true,
            enabled = true,
            label = { Text(stringResource(R.string.model_local_path_title)) },
//            trailingIcon = {
//                IconButton(
//                    onClick = {
//                        processIntent(
//                            ServerSetupIntent.SelectLocalModelPath(LOCAL_DIFFUSION_CUSTOM_PATH)
//                        )
//                    },
//                    content = {
//                        Icon(
//                            imageVector = Icons.Default.Refresh,
//                            contentDescription = "Reset",
//                        )
//                    },
//                )
//            },
//            isError = state.localCustomModelPathValidationError != null,
//            supportingText = {
//                state.localCustomModelPathValidationError
//                    ?.let { Text(it.asString(), color = MaterialTheme.colorScheme.error) }
//            },
            colors = textFieldColors,
        )
        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 8.dp),
            onClick = {
                pickFileLauncher.launch(arrayOf("application/octet-stream"))
            },
        ) {
            Text(
//                text = stringResource(id = R.string.model_local_path_button),
                text = "Select file",
                color = LocalContentColor.current,
            )
        }
    }
}
