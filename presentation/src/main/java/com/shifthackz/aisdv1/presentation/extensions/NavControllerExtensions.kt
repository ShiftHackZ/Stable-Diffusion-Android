package com.shifthackz.aisdv1.presentation.extensions

import androidx.navigation.NavController
import com.shifthackz.aisdv1.presentation.navigation.NavigationRoute

fun NavController.navigatePopUpToCurrent(navRoute: NavigationRoute) {
    navigate(navRoute) {
        currentBackStackEntry?.destination?.route?.let {
            popUpTo(it) { inclusive = true }
        }
    }
}
