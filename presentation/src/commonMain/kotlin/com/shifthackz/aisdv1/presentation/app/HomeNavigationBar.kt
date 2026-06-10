package com.shifthackz.aisdv1.presentation.app

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * Carries bottom navigation item data used by [AppScaffold].
 *
 * @author Dmitriy Moroz
 */
internal data class HomeNavigationBarItem(
    /**
     * Text displayed under the item icon.
     *
     * @author Dmitriy Moroz
     */
    val label: String,
    /**
     * Whether this item represents the current route.
     *
     * @author Dmitriy Moroz
     */
    val selected: Boolean,
    /**
     * Icon displayed above the item label.
     *
     * @author Dmitriy Moroz
     */
    val icon: @Composable () -> Unit,
    /**
     * Action invoked when the item is selected.
     *
     * @author Dmitriy Moroz
     */
    val onClick: () -> Unit,
)

/**
 * Renders the app bottom navigation bar.
 *
 * @param items route items displayed in the bottom navigation bar.
 * @author Dmitriy Moroz
 */
@Composable
internal fun HomeNavigationBar(
    items: List<HomeNavigationBarItem>,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = item.selected,
                label = {
                    Text(
                        text = item.label,
                        color = LocalContentColor.current,
                    )
                },
                colors = NavigationBarItemDefaults.colors().copy(
                    selectedIndicatorColor = MaterialTheme.colorScheme.primary,
                ),
                icon = item.icon,
                onClick = item.onClick,
            )
        }
    }
}
