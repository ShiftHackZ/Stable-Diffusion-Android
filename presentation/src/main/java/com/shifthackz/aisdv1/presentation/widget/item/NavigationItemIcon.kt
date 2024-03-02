package com.shifthackz.aisdv1.presentation.widget.item

import androidx.compose.foundation.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import com.shifthackz.aisdv1.presentation.model.NavItem

@Composable
fun NavigationItemIcon(icon: NavItem.Icon) {
    when (icon) {
        is NavItem.Icon.Resource -> Image(
            modifier = icon.modifier,
            painter = painterResource(icon.resId),
            contentDescription = null,
            colorFilter = ColorFilter.tint(LocalContentColor.current),
        )
        is NavItem.Icon.Vector -> Icon(
            modifier = icon.modifier,
            imageVector = icon.vector,
            contentDescription = null,
            tint = LocalContentColor.current,
        )
    }
}
