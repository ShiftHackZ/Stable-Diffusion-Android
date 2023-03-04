package com.shifthackz.aisdv1.presentation.screen.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class HomeNavigationItem(
    val name: String,
    val route: String,
    val icon: ImageVector = Icons.Rounded.Home,
    val content: @Composable () -> Unit,
)
