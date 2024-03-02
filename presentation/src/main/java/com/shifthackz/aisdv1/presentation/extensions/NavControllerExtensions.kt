package com.shifthackz.aisdv1.presentation.extensions

import androidx.navigation.NavController

fun NavController.navigatePopUpToCurrent(route: String) {
    navigate(route) {
        currentBackStackEntry?.destination?.route?.let {
            popUpTo(it) { inclusive = true }
        }
    }
}
