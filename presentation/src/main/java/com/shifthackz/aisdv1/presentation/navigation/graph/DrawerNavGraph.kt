package com.shifthackz.aisdv1.presentation.navigation.graph

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.SettingsEthernet
import androidx.compose.material.icons.filled.Web
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.domain.entity.FeatureTag
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.Settings
import com.shifthackz.aisdv1.presentation.model.LaunchSource
import com.shifthackz.aisdv1.presentation.model.NavItem
import com.shifthackz.aisdv1.presentation.navigation.NavigationRoute
import com.shifthackz.aisdv1.presentation.widget.source.getNameUiText
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

fun mainDrawerNavItems(settings: Settings? = null): List<NavItem> = buildList {
    add(txt2ImgTab().copy(name = LocalizationR.string.title_text_to_image.asUiText()))
    add(img2imgTab().copy(name = LocalizationR.string.title_image_to_image.asUiText()))
    add(galleryTab())
    settings?.source?.takeIf { it.featureTags.contains(FeatureTag.OwnServer) }?.let {
        add(webUi(it))
    }
    add(settingsTab())
    add(configuration())
    settings?.developerMode?.takeIf { it }?.let {
        add(developerMode())
    }
}

private fun webUi(source: ServerSource) = NavItem(
    name = UiText.Concat(
        LocalizationR.string.drawer_web_ui.asUiText(),
        " (".asUiText(),
        source.getNameUiText(),
        ")".asUiText(),
    ),
    navRoute = NavigationRoute.WebUi,
    icon = NavItem.Icon.Vector(
        vector = Icons.Default.Web,
    ),
)

private fun configuration() = NavItem(
    name = LocalizationR.string.settings_item_config.asUiText(),
    navRoute = NavigationRoute.ServerSetup(source = LaunchSource.SETTINGS),
    icon = NavItem.Icon.Vector(
        vector = Icons.Default.SettingsEthernet,
    ),
)

private fun developerMode() = NavItem(
    name = LocalizationR.string.title_debug_menu.asUiText(),
    navRoute = NavigationRoute.Debug,
    icon = NavItem.Icon.Vector(
        vector = Icons.Default.DeveloperMode,
    )
)
