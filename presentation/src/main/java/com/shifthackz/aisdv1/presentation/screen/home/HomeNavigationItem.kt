package com.shifthackz.aisdv1.presentation.screen.home

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class HomeNavigationItem(
    val name: String,
    val route: String,
    val icon: HomeNavigationItem.Icon,
    val content: @Composable () -> Unit,
) {
    sealed interface Icon {
        data class Resource(@DrawableRes val resId: Int) : Icon
        data class Vector(val vector: ImageVector) : Icon
    }
}
