@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.mvi.MviComponent
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.di.initKoin
import com.shifthackz.aisdv1.presentation.generated.resources.Res
import com.shifthackz.aisdv1.presentation.generated.resources.ic_share
import com.shifthackz.aisdv1.presentation.navigation.router.GalleryDetailRouter
import com.shifthackz.aisdv1.presentation.widget.dialog.DecisionInteractiveDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ErrorDialog
import com.shifthackz.aisdv1.presentation.widget.image.ZoomableImage
import com.shifthackz.aisdv1.presentation.widget.image.ZoomableImageSource
import com.shifthackz.aisdv1.presentation.widget.scrollbar.verticalScrollbar
import org.jetbrains.compose.resources.painterResource
import org.koin.core.parameter.parametersOf


/**
 * Renders the `GalleryDetailDialogRenderer` UI for the SDAI presentation layer.
 *
 * @param dialog dialog value consumed by the API.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal fun GalleryDetailDialogRenderer(
    dialog: GalleryDetailDialog,
    processIntent: (GalleryDetailIntent) -> Unit,
) {
    when (dialog) {
        GalleryDetailDialog.None -> Unit
        GalleryDetailDialog.DeleteConfirm -> DecisionInteractiveDialog(
            title = Localization.string("interaction_delete_generation_title").asUiText(),
            text = Localization.string("interaction_delete_generation_sub_title").asUiText(),
            confirmActionText = Localization.string("yes").asUiText(),
            dismissActionText = Localization.string("no").asUiText(),
            onConfirmAction = { processIntent(GalleryDetailIntent.Delete.Confirm) },
            onDismissRequest = { processIntent(GalleryDetailIntent.DismissDialog) },
        )
        is GalleryDetailDialog.Error -> ErrorDialog(
            text = dialog.message.asUiText(),
            onDismissRequest = { processIntent(GalleryDetailIntent.DismissDialog) },
        )
    }
}

/**
 * Exposes the `GalleryDetailTab` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal val GalleryDetailTab.label: String
    get() = when (this) {
        GalleryDetailTab.IMAGE -> Localization.string("gallery_tab_image")
        GalleryDetailTab.ORIGINAL -> Localization.string("gallery_tab_original")
        GalleryDetailTab.INFO -> Localization.string("gallery_tab_info")
    }

/**
 * Exposes the `GalleryDetailTab` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal val GalleryDetailTab.icon: ImageVector
    get() = when (this) {
        GalleryDetailTab.IMAGE -> Icons.Default.Image
        GalleryDetailTab.ORIGINAL -> Icons.Default.Image
        GalleryDetailTab.INFO -> Icons.Default.Info
    }
