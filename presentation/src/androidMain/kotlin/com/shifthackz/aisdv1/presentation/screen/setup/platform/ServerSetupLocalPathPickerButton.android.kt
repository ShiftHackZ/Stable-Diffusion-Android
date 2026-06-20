package com.shifthackz.aisdv1.presentation.screen.setup.platform

import android.content.Intent
import android.os.Build
import android.os.Environment
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

internal actual fun isLocalGenerationSetupAvailable(): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager()

internal actual fun isServerSourceAvailableOnPlatform(source: ServerSource): Boolean = when (source) {
    ServerSource.LOCAL_APPLE_BONSAI -> isAndroidBonsaiSupportedInPrinciple(Build.SUPPORTED_64_BIT_ABIS)
    else -> true
}

internal fun isAndroidBonsaiSupportedInPrinciple(supported64BitAbis: Array<String>?): Boolean =
    supported64BitAbis.orEmpty().any { abi -> abi == ANDROID_BONSAI_ABI }

private const val ANDROID_BONSAI_ABI = "arm64-v8a"
