package com.shifthackz.aisdv1.presentation.screen.donate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.mvi.MviComponent
import com.shifthackz.aisdv1.presentation.di.initKoin
import com.shifthackz.aisdv1.presentation.navigation.router.DonateRouter
import com.shifthackz.aisdv1.presentation.platform.rememberExternalUrlLauncher
import org.koin.core.parameter.parametersOf

/**
 * Renders the `DonateScreen` UI for the SDAI presentation layer.
 *
 * @param router router value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
fun DonateScreen(
    router: DonateRouter? = null,
) {
    val koin = remember { initKoin() }
    val urlLauncher = rememberExternalUrlLauncher()
    val donateRouter = remember(koin, router) {
        router ?: koin.get<DonateRouter>()
    }
    val viewModel = remember(koin, donateRouter) {
        koin.get<DonateViewModel> {
            parametersOf(donateRouter)
        }
    }
    MviComponent(
        viewModel = viewModel,
        processEffect = { effect ->
            when (effect) {
                is DonateEffect.OpenUrl -> urlLauncher.openUrl(effect.url)
            }
        },
    ) { state, processIntent ->
        val strings = donateScreenStrings()
        DonateScreenContent(
            state = state,
            strings = strings,
            processIntent = processIntent,
            brandIcon = { DonateBrandIcon() },
        )
    }
}

/**
 * Renders the `donateScreenStrings` UI for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Composable
private fun donateScreenStrings() = DonateScreenStrings(
    title = Localization.string("settings_item_donate"),
    thanksTitle = Localization.string("donate_title_thanks"),
    bottomTitle = Localization.string("donate_bs_title"),
    bottomSubtitle = Localization.string("donate_bs_sub_title"),
    bottomEnding = Localization.string("donate_bs_ending"),
    backContentDescription = Localization.string("action_back"),
)

/**
 * Renders the `DonateBrandIcon` UI for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Composable
internal expect fun DonateBrandIcon()
