package com.shifthackz.aisdv1.presentation.screen.settings

import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.domain.entity.ColorToken
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken
import com.shifthackz.aisdv1.domain.entity.Grid
import com.shifthackz.aisdv1.presentation.screen.drawer.DrawerIntent
import com.shifthackz.android.core.mvi.MviIntent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed interface SettingsIntent : MviIntent {

    data object NavigateConfiguration : SettingsIntent

    data object NavigateDeveloperMode : SettingsIntent

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

        sealed interface GalleryGrid : Action {

            data object Pick : GalleryGrid

            data class Set(val grid: Grid) : GalleryGrid
        }

        data object Donate : Action

        data object OnBoarding : Action
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

        data class BackgroundGeneration(override val flag: Boolean) : UpdateFlag

        data class SaveToMediaStore(override val flag: Boolean) : UpdateFlag

        data class TaggedInput(override val flag: Boolean) : UpdateFlag

        data class AdvancedFormVisibility(override val flag: Boolean) : UpdateFlag

        data class DynamicColors(override val flag: Boolean) : UpdateFlag

        data class SystemDarkTheme(override val flag: Boolean) : UpdateFlag

        data class DarkTheme(override val flag: Boolean) : UpdateFlag
    }

    data class NewColorToken(val token: ColorToken) : SettingsIntent

    data class NewDarkThemeToken(val token: DarkThemeToken) : SettingsIntent

    sealed interface Permission : SettingsIntent {
        val isGranted: Boolean

        data class Storage(override val isGranted: Boolean) : Permission

        data class Notification(override val isGranted: Boolean) : Permission
    }

    data object DismissDialog : SettingsIntent

    data class Drawer(val intent: DrawerIntent) : SettingsIntent
}
