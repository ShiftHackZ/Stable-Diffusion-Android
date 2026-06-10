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
fun GalleryDetailScreenContent(
    modifier: Modifier = Modifier,
    state: GalleryDetailState,
    processIntent: (GalleryDetailIntent) -> Unit = {},
) {
    Box(modifier = modifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(Localization.string("title_gallery_details"))
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { processIntent(GalleryDetailIntent.NavigateBack) },
                        ) {
                            Icon(
                                Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = Localization.string("action_back"),
                            )
                        }
                    },
                    actions = {
                        AnimatedVisibility(
                            visible = state.content != null && state.selectedTab != GalleryDetailTab.INFO,
                            enter = fadeIn(),
                            exit = fadeOut(),
                        ) {
                            IconButton(
                                onClick = { processIntent(GalleryDetailIntent.Share.Image) },
                            ) {
                                Icon(
                                    modifier = Modifier.size(24.dp),
                                    painter = painterResource(Res.drawable.ic_share),
                                    contentDescription = Localization.string("action_share_image"),
                                    tint = LocalContentColor.current,
                                )
                            }
                        }
                    },
                )
            },
            content = { paddingValues ->
                val contentModifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)

                if (state.content != null) {
                    GalleryDetailContentState(
                        modifier = contentModifier,
                        state = state,
                        content = state.content,
                        onCopyTextClick = { value ->
                            processIntent(GalleryDetailIntent.CopyToClipboard(value))
                        },
                    )
                }
            },
            bottomBar = {
                GalleryDetailNavigationBar(
                    state = state,
                    processIntent = processIntent,
                )
            },
        )
        GalleryDetailDialogRenderer(
            dialog = state.dialog,
            processIntent = processIntent,
        )
    }
}

@Composable
internal fun GalleryDetailNavigationBar(
    state: GalleryDetailState,
    processIntent: (GalleryDetailIntent) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        state.content?.let { content ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                GalleryDetailActionButton(
                    modifier = Modifier.weight(1f),
                    icon = {
                        Icon(
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            imageVector = Icons.Default.Edit,
                            contentDescription = Localization.string("action_send_to_txt2img"),
                        )
                    },
                    label = Localization.string("home_tab_txt_to_img"),
                    onClick = { processIntent(GalleryDetailIntent.SendTo.Txt2Img) },
                )
                GalleryDetailActionButton(
                    modifier = Modifier.weight(1f),
                    icon = {
                        Icon(
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            imageVector = Icons.Default.Image,
                            contentDescription = Localization.string("action_send_to_img2img"),
                        )
                    },
                    label = Localization.string("home_tab_img_to_img"),
                    onClick = { processIntent(GalleryDetailIntent.SendTo.Img2Img) },
                )
                if (content.showReportButton) {
                    GalleryDetailActionButton(
                        modifier = Modifier.weight(1f),
                        icon = {
                            Icon(
                                modifier = Modifier.size(ButtonDefaults.IconSize),
                                imageVector = Icons.Default.Report,
                                contentDescription = Localization.string("report_title"),
                            )
                        },
                        label = Localization.string("gallery_action_report"),
                        onClick = { processIntent(GalleryDetailIntent.Report) },
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                GalleryDetailActionButton(
                    modifier = Modifier.weight(1f),
                    icon = {
                        Icon(
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            imageVector = if (content.hidden) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = Localization.string("gallery_action_blur"),
                        )
                    },
                    label = Localization.string("gallery_action_blur"),
                    onClick = { processIntent(GalleryDetailIntent.ToggleVisibility) },
                )
                GalleryDetailActionButton(
                    modifier = Modifier.weight(1f),
                    icon = {
                        Icon(
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            imageVector = Icons.Default.Share,
                            contentDescription = Localization.string("action_share_prompt"),
                        )
                    },
                    label = Localization.string("hint_prompt"),
                    onClick = { processIntent(GalleryDetailIntent.Share.Params) },
                )
                GalleryDetailActionButton(
                    modifier = Modifier.weight(1f),
                    icon = {
                        Icon(
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            imageVector = Icons.Default.Delete,
                            contentDescription = Localization.string("action_delete_image"),
                        )
                    },
                    label = Localization.string("gallery_action_delete"),
                    onClick = { processIntent(GalleryDetailIntent.Delete.Request) },
                )
            }
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                state.tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = state.selectedTab == tab,
                        label = {
                            Text(
                                text = tab.label,
                                color = LocalContentColor.current,
                            )
                        },
                        colors = NavigationBarItemDefaults.colors().copy(
                            selectedIndicatorColor = MaterialTheme.colorScheme.primary,
                        ),
                        icon = {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                            )
                        },
                        onClick = { processIntent(GalleryDetailIntent.SelectTab(tab)) },
                    )
                }
            }
        }
    }
}

@Composable
internal fun GalleryDetailActionButton(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    label: String,
    onClick: () -> Unit,
) {
    OutlinedButton(
        modifier = modifier.height(42.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
        onClick = onClick,
    ) {
        icon()
        Text(
            modifier = Modifier.padding(start = 6.dp),
            text = label,
            color = LocalContentColor.current,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
internal fun GalleryDetailContentState(
    modifier: Modifier = Modifier,
    state: GalleryDetailState,
    content: GalleryDetailContent,
    onCopyTextClick: (String) -> Unit = {},
) {
    when (state.selectedTab) {
        GalleryDetailTab.IMAGE -> {
            val image = content.image
            if (image != null) {
                ZoomableImage(
                    modifier = modifier,
                    source = ZoomableImageSource.Bitmap(image),
                    hideImage = content.hidden,
                    fitToWidth = true,
                )
            }
        }
        GalleryDetailTab.ORIGINAL -> {
            val inputImage = content.inputImage
            if (inputImage != null) {
                ZoomableImage(
                    modifier = modifier,
                    source = ZoomableImageSource.Bitmap(inputImage),
                    fitToWidth = true,
                )
            }
        }
        GalleryDetailTab.INFO -> GalleryDetailsTable(
            modifier = modifier,
            content = content,
            onCopyTextClick = onCopyTextClick,
        )
    }
}
