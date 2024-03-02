package com.shifthackz.aisdv1.presentation.screen.drawer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import com.shifthackz.aisdv1.core.ui.MviComponent
import com.shifthackz.aisdv1.presentation.model.NavItem
import com.shifthackz.aisdv1.presentation.widget.item.NavigationItemIcon
import org.koin.androidx.compose.koinViewModel

@Composable
fun DrawerScreen(
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    backStackEntry: State<NavBackStackEntry?>,
    navItems: List<NavItem> = emptyList(),
    onNavigate: (String) -> Unit = {},
    content: @Composable () -> Unit,
) {
    MviComponent(viewModel = koinViewModel<DrawerViewModel>()) { _, intentHandler ->
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Spacer(modifier = Modifier.height(16.dp))
                    val currentRoute = backStackEntry.value?.destination?.route
                    navItems.forEach { item ->
                        val selected = item.route == currentRoute
                        NavigationDrawerItem(
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                            selected = selected,
                            label = {
                                Text(
                                    text = item.name,
                                    color = LocalContentColor.current,
                                )
                            },
                            icon = { NavigationItemIcon(item.icon) },
                            onClick = {
                                if (!selected) {
                                    onNavigate(item.route)
                                    intentHandler(DrawerIntent.Close)
                                }
                            },
                        )
                    }
                }
            },
        ) {
            content()
        }
    }
}
