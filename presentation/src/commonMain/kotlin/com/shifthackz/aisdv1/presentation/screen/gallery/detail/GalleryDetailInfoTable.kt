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


@Composable
internal fun GalleryDetailsTable(
    modifier: Modifier = Modifier,
    content: GalleryDetailContent,
    onCopyTextClick: (String) -> Unit = {},
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .verticalScrollbar(scrollState)
            .verticalScroll(scrollState)
            .fillMaxSize(),
    ) {
        val colorOddBg = MaterialTheme.colorScheme.surface
        val colorEvenBg = MaterialTheme.colorScheme.surfaceTint
        val colorText = MaterialTheme.colorScheme.onSurface
        GalleryDetailRow(
            modifier = Modifier.background(color = colorOddBg),
            name = Localization.string("gallery_info_field_date"),
            value = content.createdAt,
            color = colorText,
            onCopyTextClick = onCopyTextClick,
        )
        GalleryDetailRow(
            modifier = Modifier.background(color = colorEvenBg),
            name = Localization.string("gallery_info_field_type"),
            value = content.type,
            color = colorText,
            onCopyTextClick = onCopyTextClick,
        )
        GalleryDetailRow(
            modifier = Modifier.background(color = colorOddBg),
            name = Localization.string("gallery_info_field_prompt"),
            value = content.prompt,
            color = colorText,
            onCopyTextClick = onCopyTextClick,
        )
        GalleryDetailRow(
            modifier = Modifier.background(color = colorEvenBg),
            name = Localization.string("gallery_info_field_negative_prompt"),
            value = content.negativePrompt,
            color = colorText,
            onCopyTextClick = onCopyTextClick,
        )
        GalleryDetailRow(
            modifier = Modifier.background(color = colorOddBg),
            name = Localization.string("gallery_info_field_size"),
            value = content.size,
            color = colorText,
            onCopyTextClick = onCopyTextClick,
        )
        GalleryDetailRow(
            modifier = Modifier.background(color = colorEvenBg),
            name = Localization.string("gallery_info_field_sampling_steps"),
            value = content.samplingSteps,
            color = colorText,
            onCopyTextClick = onCopyTextClick,
        )
        GalleryDetailRow(
            modifier = Modifier.background(color = colorOddBg),
            name = Localization.string("gallery_info_field_cfg"),
            value = content.cfgScale,
            color = colorText,
            onCopyTextClick = onCopyTextClick,
        )
        GalleryDetailRow(
            modifier = Modifier.background(color = colorEvenBg),
            name = Localization.string("gallery_info_field_restore_faces"),
            value = content.restoreFaces,
            color = colorText,
            onCopyTextClick = onCopyTextClick,
        )
        GalleryDetailRow(
            modifier = Modifier.background(color = colorOddBg),
            name = Localization.string("gallery_info_field_sampler"),
            value = content.sampler,
            color = colorText,
            onCopyTextClick = onCopyTextClick,
        )
        GalleryDetailRow(
            modifier = Modifier.background(color = colorEvenBg),
            name = Localization.string("gallery_info_field_seed"),
            value = content.seed,
            color = colorText,
            onCopyTextClick = onCopyTextClick,
        )
        GalleryDetailRow(
            modifier = Modifier.background(color = colorOddBg),
            name = Localization.string("gallery_info_field_sub_seed"),
            value = content.subSeed,
            color = colorText,
            onCopyTextClick = onCopyTextClick,
        )
        GalleryDetailRow(
            modifier = Modifier.background(color = colorEvenBg),
            name = Localization.string("gallery_info_field_sub_seed_strength"),
            value = content.subSeedStrength,
            color = colorText,
            onCopyTextClick = onCopyTextClick,
        )
        if (content.generationType == AiGenerationResult.Type.IMAGE_TO_IMAGE) {
            GalleryDetailRow(
                modifier = Modifier.background(color = colorOddBg),
                name = Localization.string("gallery_info_field_denoising_strength"),
                value = content.denoisingStrength,
                color = colorText,
                onCopyTextClick = onCopyTextClick,
            )
        }
    }
}

@Composable
internal fun GalleryDetailRow(
    modifier: Modifier = Modifier,
    column1Weight: Float = 0.4f,
    column2Weight: Float = 0.6f,
    name: String,
    value: String,
    color: Color,
    onCopyTextClick: (String) -> Unit = {},
) {
    Row(modifier) {
        GalleryDetailCell(
            text = name,
            modifier = Modifier.weight(column1Weight),
            color = color,
        )
        GalleryDetailCell(
            text = value,
            modifier = Modifier.weight(column2Weight),
            color = color,
        )
        if (value.isNotBlank()) {
            IconButton(
                onClick = { onCopyTextClick(value) },
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = Localization.string("action_copy"),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
internal fun GalleryDetailCell(
    modifier: Modifier = Modifier,
    text: String,
    color: Color,
) {
    Text(
        modifier = modifier
            .padding(start = 12.dp)
            .padding(vertical = 8.dp),
        text = text,
        color = color,
    )
}
