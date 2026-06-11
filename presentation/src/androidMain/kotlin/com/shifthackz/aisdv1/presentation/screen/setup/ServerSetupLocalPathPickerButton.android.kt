package com.shifthackz.aisdv1.presentation.screen.setup

import android.content.Intent
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.shifthackz.aisdv1.core.extensions.getRealPath
import com.shifthackz.aisdv1.domain.entity.ServerSource

/**
 * Renders the `ServerSetupLocalPathPickerButton` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param text text value consumed by the API.
 * @param onPathSelected callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
internal actual fun ServerSetupLocalPathPickerButton(
    modifier: Modifier,
    text: String,
    onPathSelected: (String) -> Unit,
) {
    val context = LocalContext.current
    val uriFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        result.data?.data?.let { uri ->
            context.contentResolver.takePersistableUriPermission(uri, uriFlags)
            val documentUri = DocumentsContract.buildDocumentUriUsingTree(
                uri,
                DocumentsContract.getTreeDocumentId(uri),
            )
            getRealPath(context, documentUri)?.let(onPathSelected)
        }
    }
    OutlinedButton(
        modifier = modifier,
        onClick = {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                addFlags(uriFlags)
            }
            launcher.launch(intent)
        },
    ) {
        Text(text = text)
    }
}

/**
 * Executes the `isLocalGenerationSetupAvailable` step in the SDAI presentation layer.
 *
 * @return Result produced by `isLocalGenerationSetupAvailable`.
 * @author Dmitriy Moroz
 */
internal actual fun isLocalGenerationSetupAvailable(): Boolean = true

/**
 * Executes the `isServerSourceAvailableOnPlatform` step in the SDAI presentation layer.
 *
 * @param source source value consumed by the API.
 * @return Result produced by `isServerSourceAvailableOnPlatform`.
 * @author Dmitriy Moroz
 */
internal actual fun isServerSourceAvailableOnPlatform(source: ServerSource): Boolean =
    source != ServerSource.LOCAL_APPLE_CORE_ML
