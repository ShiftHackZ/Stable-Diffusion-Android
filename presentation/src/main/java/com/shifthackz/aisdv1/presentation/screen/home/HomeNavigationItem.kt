package com.shifthackz.aisdv1.presentation.screen.home

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

data class HomeNavigationItem(
    val name: String,
    val route: String,
    val icon: HomeNavigationItem.Icon,
    val content: @Composable () -> Unit,
) {
    sealed interface Icon {
        val modifier: Modifier

        data class Resource(
            override val modifier: Modifier = Modifier,
            @DrawableRes val resId: Int,
        ) : Icon

        data class Vector(
            override val modifier: Modifier = Modifier,
            val vector: ImageVector,
        ) : Icon
    }
}
