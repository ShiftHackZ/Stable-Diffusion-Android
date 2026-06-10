package com.shifthackz.aisdv1.presentation.screen.home

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * Carries `HomeNavigationBarItem` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
data class HomeNavigationBarItem(
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
 * Renders the `HomeNavigationBar` UI for the SDAI presentation layer.
 *
 * @param items items value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
fun HomeNavigationBar(
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
