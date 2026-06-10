@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileDownloadDone
import androidx.compose.material.icons.outlined.FileDownloadOff
import androidx.compose.material.icons.outlined.Landslide
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.common.file.LOCAL_DIFFUSION_CUSTOM_PATH
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.modal.download.DownloadDialog
import com.shifthackz.aisdv1.presentation.screen.setup.mappers.mapToUi
import com.shifthackz.aisdv1.presentation.theme.textFieldColors
import com.shifthackz.aisdv1.presentation.widget.dialog.ErrorDialogContent
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialog
import com.shifthackz.aisdv1.presentation.widget.input.DropdownTextField
import com.shifthackz.aisdv1.presentation.widget.input.PlatformOutlinedTextField
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem
import com.shifthackz.aisdv1.presentation.widget.scrollbar.verticalScrollbar
import kotlinx.coroutines.launch



@Composable
internal fun FormTitle(
    title: String,
    subtitle: String? = null,
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp),
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
    )
    subtitle?.let {
        Text(
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            text = it,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
internal fun HintText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier.padding(top = 8.dp, bottom = 16.dp),
        text = text,
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
internal fun AuthFields(
    state: ServerSetupState,
    strings: ServerSetupStrings,
    processIntent: (ServerSetupIntent) -> Unit,
) {
    Text(
        modifier = Modifier.padding(top = 4.dp),
        text = strings.authTitle,
        style = MaterialTheme.typography.labelLarge,
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ServerSetupState.AuthType.entries.forEach { type ->
            FilterChip(
                selected = state.authType == type,
                onClick = {
                    processIntent(ServerSetupIntent.UpdateAuthType(type))
                },
                label = {
                    Text(
                        text = when (type) {
                            ServerSetupState.AuthType.ANONYMOUS -> strings.anonymous
                            ServerSetupState.AuthType.HTTP_BASIC -> strings.httpBasic
                        },
                    )
                },
            )
        }
    }
    if (state.authType == ServerSetupState.AuthType.HTTP_BASIC) {
        SetupTextField(
            value = state.login,
            onValueChange = { processIntent(ServerSetupIntent.UpdateLogin(it)) },
            label = strings.login,
            error = state.loginValidationError?.message(strings),
        )
        SetupTextField(
            value = state.password,
            onValueChange = { processIntent(ServerSetupIntent.UpdatePassword(it)) },
            label = strings.password,
            error = state.passwordValidationError?.message(strings),
            keyboardType = KeyboardType.Password,
            visualTransformation = if (state.passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                val image = if (state.passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                val description = if (state.passwordVisible) strings.hidePassword else strings.showPassword
                IconButton(
                    onClick = {
                        processIntent(
                            ServerSetupIntent.UpdatePasswordVisibility(
                                state.passwordVisible,
                            ),
                        )
                    },
                ) {
                    Icon(
                        imageVector = image,
                        contentDescription = description,
                    )
                }
            },
        )
    }
}

@Composable
internal fun SetupTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    PlatformOutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        value = value,
        onValueChange = onValueChange,
        label = label,
        containerColor = MaterialTheme.colorScheme.background,
        textColor = MaterialTheme.colorScheme.onSurface,
        hintColor = MaterialTheme.colorScheme.onSurfaceVariant,
        enabled = enabled,
        error = error,
        keyboardType = keyboardType,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        singleLine = true,
    )
}

