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

data class DrawerSheetItem(
    val label: String,
    val selected: Boolean,
    val icon: @Composable () -> Unit,
    val onClick: () -> Unit,
)

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
