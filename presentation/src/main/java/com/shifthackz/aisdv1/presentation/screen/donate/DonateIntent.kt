package com.shifthackz.aisdv1.presentation.screen.donate

import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.android.core.mvi.MviIntent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed interface DonateIntent : MviIntent {

    data object NavigateBack : DonateIntent

    sealed class LaunchUrl : DonateIntent, KoinComponent {
        protected val linksProvider: LinksProvider by inject()
        abstract val url: String

        data object DonateBmc : LaunchUrl() {
            override val url: String
                get() = linksProvider.donateUrl
        }
    }
}
