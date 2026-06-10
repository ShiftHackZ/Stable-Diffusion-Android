package com.shifthackz.aisdv1.presentation.screen.drawer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Carries `DrawerSheetItem` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
data class DrawerSheetItem(
    /**
     * Exposes the `label` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val label: String,
    /**
     * Exposes the `selected` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val selected: Boolean,
    /**
     * Exposes the `icon` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val icon: @Composable () -> Unit,
    /**
     * Exposes the `onClick` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val onClick: () -> Unit,
)

/**
 * Renders the `DrawerSheetContent` UI for the SDAI presentation layer.
 *
 * @param items items value consumed by the API.
 * @param header header value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
fun DrawerSheetContent(
    items: List<DrawerSheetItem>,
    header: @Composable () -> Unit,
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.background,
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        val itemModifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        header()

        Spacer(modifier = Modifier.height(32.dp))
        items.forEach { item ->
            NavigationDrawerItem(
                modifier = itemModifier.padding(bottom = 4.dp),
                selected = item.selected,
                label = {
                    Text(
                        text = item.label,
                        color = LocalContentColor.current,
                    )
                },
                icon = item.icon,
                onClick = item.onClick,
            )
        }
    }
}
