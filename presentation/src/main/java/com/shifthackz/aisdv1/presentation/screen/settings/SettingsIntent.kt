package com.shifthackz.aisdv1.presentation.screen.settings

import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.domain.entity.ColorToken
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken
import com.shifthackz.aisdv1.presentation.screen.drawer.DrawerIntent
import com.shifthackz.android.core.mvi.MviIntent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed interface SettingsIntent : MviIntent {

    data object NavigateConfiguration : SettingsIntent

    sealed interface SdModel : SettingsIntent {
        data object OpenChooser : SdModel

        data class Select(val model: String) : SdModel
    }

    sealed interface Action : SettingsIntent {
        enum class ClearAppCache : Action {
            Request, Confirm
        }

        data object ReportProblem : Action

        data object AppVersion : Action

        data object PickLanguage : Action

        data object Donate : Action
    }

    sealed class LaunchUrl : SettingsIntent, KoinComponent {

        protected val linksProvider: LinksProvider by inject()
        abstract val url: String

        data object OpenPolicy : LaunchUrl() {
            override val url: String
                get() = linksProvider.privacyPolicyUrl
        }

        data object OpenSourceCode : LaunchUrl() {
            override val url: String
                get() = linksProvider.gitHubSourceUrl
        }
    }

    sealed interface UpdateFlag : SettingsIntent {

        val flag: Boolean

        data class NNAPI(override val flag: Boolean) : UpdateFlag

        data class MonitorConnection(override val flag: Boolean) : UpdateFlag

        data class AutoSaveResult(override val flag: Boolean) : UpdateFlag

        data class SaveToMediaStore(override val flag: Boolean) : UpdateFlag

        data class TaggedInput(override val flag: Boolean) : UpdateFlag

        data class AdvancedFormVisibility(override val flag: Boolean) : UpdateFlag

        data class DynamicColors(override val flag: Boolean) : UpdateFlag

        data class SystemDarkTheme(override val flag: Boolean) : UpdateFlag

        data class DarkTheme(override val flag: Boolean) : UpdateFlag
    }

    data class NewColorToken(val token: ColorToken) : SettingsIntent

    data class NewDarkThemeToken(val token: DarkThemeToken) : SettingsIntent

    data object StoragePermissionGranted : SettingsIntent

    data object DismissDialog : SettingsIntent

    data class Drawer(val intent: DrawerIntent) : SettingsIntent
}
