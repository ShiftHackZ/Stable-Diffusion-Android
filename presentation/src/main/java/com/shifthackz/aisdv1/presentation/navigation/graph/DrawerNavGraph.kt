package com.shifthackz.aisdv1.presentation.navigation.graph

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SettingsEthernet
import androidx.compose.material.icons.filled.Web
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.domain.entity.FeatureTag
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.Settings
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.model.NavItem
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupLaunchSource
import com.shifthackz.aisdv1.presentation.utils.Constants
import com.shifthackz.aisdv1.presentation.widget.source.getNameUiText

fun mainDrawerNavItems(settings: Settings? = null): List<NavItem> = buildList {
    add(txt2ImgTab())
    add(img2imgTab())
    add(galleryTab())
    settings?.source?.takeIf { it.featureTags.contains(FeatureTag.OwnServer) }?.let {
        add(webUi(it))
    }
    add(settingsTab())
    add(configuration())
}

private fun webUi(source: ServerSource) = NavItem(
    name = UiText.Concat(
        R.string.drawer_web_ui.asUiText(),
        " (".asUiText(),
        source.getNameUiText(),
        ")".asUiText()
    ),
    route = Constants.ROUTE_WEB_UI,
    icon = NavItem.Icon.Vector(
        vector = Icons.Default.Web,
    ),
)

private fun configuration() = NavItem(
    name = R.string.settings_item_config.asUiText(),
    route = "${Constants.ROUTE_SERVER_SETUP}/${ServerSetupLaunchSource.SETTINGS.ordinal}",
    icon = NavItem.Icon.Vector(
        vector = Icons.Default.SettingsEthernet,
    ),
)
