package com.shifthackz.aisdv1.presentation.screen.home

import androidx.lifecycle.ViewModel
import com.shifthackz.aisdv1.domain.feature.analytics.Analytics
import com.shifthackz.aisdv1.presentation.features.HomeNavigationItemClick

class HomeNavigationViewModel(private val analytics: Analytics) : ViewModel() {

    fun logNavItemClickEvent(item: HomeNavigationItem) {
        analytics.logEvent(HomeNavigationItemClick(item))
    }
}
