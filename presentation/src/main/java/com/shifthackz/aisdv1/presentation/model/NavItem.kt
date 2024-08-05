package com.shifthackz.aisdv1.presentation.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.shifthackz.aisdv1.core.model.UiText

@Immutable
data class NavItem(
    val name: UiText,
    val route: String,
    val icon: Icon,
    val content: (@Composable () -> Unit)? = null,
) {
    sealed interface Icon {
        val modifier: Modifier

        @Immutable
        data class Resource(
            override val modifier: Modifier = Modifier,
            @DrawableRes val resId: Int,
        ) : Icon

        @Immutable
        data class Vector(
            override val modifier: Modifier = Modifier,
            val vector: ImageVector,
        ) : Icon
    }
}
