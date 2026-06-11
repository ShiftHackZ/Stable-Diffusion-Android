@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.widget.scrollbar.verticalScrollbar


/**
 * Renders the `GalleryDetailsTable` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param content content value consumed by the API.
 * @param onCopyTextClick callback invoked by the component.
 * @author Dmitriy Moroz
 */
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
        content.detailRows().forEachIndexed { index, row ->
            GalleryDetailRow(
                modifier = Modifier.background(
                    color = if (index % 2 == 0) colorOddBg else colorEvenBg,
                ),
                name = row.name,
                value = row.value,
                color = colorText,
                onCopyTextClick = onCopyTextClick,
            )
        }
    }
}

/**
 * Renders the `GalleryDetailRow` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param column1Weight column1 weight value consumed by the API.
 * @param column2Weight column2 weight value consumed by the API.
 * @param name name value consumed by the API.
 * @param value value value consumed by the API.
 * @param color color value consumed by the API.
 * @param onCopyTextClick callback invoked by the component.
 * @author Dmitriy Moroz
 */
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

/**
 * Renders the `GalleryDetailCell` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param text text value consumed by the API.
 * @param color color value consumed by the API.
 * @author Dmitriy Moroz
 */
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

private fun GalleryDetailContent.detailRows(): List<GalleryDetailField> =
    buildList {
        add(
            GalleryDetailField(
                name = Localization.string("gallery_info_field_date"),
                value = createdAt,
            )
        )
        add(
            GalleryDetailField(
                name = Localization.string("gallery_info_field_type"),
                value = type,
            )
        )
        if (modelName.isNotBlank()) {
            add(
                GalleryDetailField(
                    name = Localization.string("gallery_info_field_model"),
                    value = modelName,
                )
            )
        }
        add(
            GalleryDetailField(
                name = Localization.string("gallery_info_field_prompt"),
                value = prompt,
            )
        )
        add(
            GalleryDetailField(
                name = Localization.string("gallery_info_field_negative_prompt"),
                value = negativePrompt,
            )
        )
        add(
            GalleryDetailField(
                name = Localization.string("gallery_info_field_size"),
                value = this@detailRows.size,
            )
        )
        add(
            GalleryDetailField(
                name = Localization.string("gallery_info_field_sampling_steps"),
                value = samplingSteps,
            )
        )
        add(
            GalleryDetailField(
                name = Localization.string("gallery_info_field_cfg"),
                value = cfgScale,
            )
        )
        add(
            GalleryDetailField(
                name = Localization.string("gallery_info_field_restore_faces"),
                value = restoreFaces,
            )
        )
        add(
            GalleryDetailField(
                name = Localization.string("gallery_info_field_sampler"),
                value = sampler,
            )
        )
        add(
            GalleryDetailField(
                name = Localization.string("gallery_info_field_seed"),
                value = seed,
            )
        )
        add(
            GalleryDetailField(
                name = Localization.string("gallery_info_field_sub_seed"),
                value = subSeed,
            )
        )
        add(
            GalleryDetailField(
                name = Localization.string("gallery_info_field_sub_seed_strength"),
                value = subSeedStrength,
            )
        )
        if (generationType == AiGenerationResult.Type.IMAGE_TO_IMAGE) {
            add(
                GalleryDetailField(
                    name = Localization.string("gallery_info_field_denoising_strength"),
                    value = denoisingStrength,
                )
            )
        }
    }

private data class GalleryDetailField(
    val name: String,
    val value: String,
)
