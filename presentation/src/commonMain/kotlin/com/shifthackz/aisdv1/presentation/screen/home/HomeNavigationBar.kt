package com.shifthackz.aisdv1.presentation.screen.home

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

data class HomeNavigationBarItem(
    val label: String,
    val selected: Boolean,
    val icon: @Composable () -> Unit,
    val onClick: () -> Unit,
)

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
