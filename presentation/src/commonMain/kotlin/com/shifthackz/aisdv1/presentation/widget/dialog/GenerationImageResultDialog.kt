package com.shifthackz.aisdv1.presentation.widget.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.screen.txt2img.decodeBase64ImageBitmap

@Composable
fun GenerationImageResultDialog(
    imageBase64: String,
    showSaveButton: Boolean = false,
    showReportButton: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onSaveRequest: () -> Unit = {},
    onReportRequest: () -> Unit = {},
    onViewDetailRequest: () -> Unit = {},
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .fillMaxWidth(0.96f),
            ) {
                val image = imageBase64.decodeBase64ImageBitmap()
                if (image != null) {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 300.dp)
                            .align(Alignment.CenterHorizontally)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                        ) { onViewDetailRequest() },
                        bitmap = image,
                        contentDescription = Localization.string("action_generate"),
                    )
                }

                if (showSaveButton) {
                    Button(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth(0.7f),
                        onClick = onSaveRequest,
                    ) {
                        Text(
                            text = Localization.string("action_save"),
                            color = LocalContentColor.current,
                        )
                    }
                }

                if (showReportButton) {
                    Button(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth(0.7f),
                        onClick = onReportRequest,
                    ) {
                        Text(
                            text = Localization.string("report_title"),
                            color = LocalContentColor.current,
                        )
                    }
                }

                OutlinedButton(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(0.7f),
                    onClick = onDismissRequest,
                ) {
                    Text(
                        text = Localization.string("action_close"),
                        color = LocalContentColor.current,
                    )
                }
            }
        }
    }
}

@Composable
fun ColumnScope.GenerationImageBatchResultModal(
    results: List<AiGenerationResult>,
    showSaveButton: Boolean = false,
    onSaveRequest: () -> Unit = {},
    onViewDetailRequest: (AiGenerationResult) -> Unit = {},
) {
    if (showSaveButton) {
        Button(
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.7f),
            onClick = onSaveRequest,
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = null,
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = Localization.string("action_save"),
                color = LocalContentColor.current,
            )
        }
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(results.size) { index ->
            val result = results[index]
            GenerationBatchImageItem(
                result = result,
                onClick = { onViewDetailRequest(result) },
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(WindowInsets.systemBars.asPaddingValues()),
    )
}

@Composable
private fun GenerationBatchImageItem(
    result: AiGenerationResult,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        result.image.decodeBase64ImageBitmap()?.let { image ->
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (!result.hidden) {
                            Modifier
                        } else {
                            Modifier.graphicsLayer {
                                renderEffect = BlurEffect(
                                    radiusX = 32f,
                                    radiusY = 32f,
                                )
                            }
                        },
                    ),
                bitmap = image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
        }
        if (result.hidden) {
            Icon(
                imageVector = Icons.Default.VisibilityOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
