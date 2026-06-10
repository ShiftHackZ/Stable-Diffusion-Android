package com.shifthackz.aisdv1.presentation.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Renders the `DrawerBrandLogo` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @author Dmitriy Moroz
 */
@Composable
internal expect fun DrawerBrandLogo(modifier: Modifier)

/**
 * Renders the `DrawerPlatformIcon` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @author Dmitriy Moroz
 */
@Composable
internal expect fun DrawerPlatformIcon(modifier: Modifier)
