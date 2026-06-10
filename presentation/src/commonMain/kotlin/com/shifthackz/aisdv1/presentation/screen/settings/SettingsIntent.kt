package com.shifthackz.aisdv1.presentation.screen.settings

import com.shifthackz.aisdv1.domain.entity.ColorToken
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken
import com.shifthackz.aisdv1.domain.entity.Grid
import com.shifthackz.aisdv1.presentation.screen.drawer.DrawerIntent
import com.shifthackz.aisdv1.core.mvi.MviIntent

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

        data class SetLanguage(val languageCode: String) : Action

        sealed interface GalleryGrid : Action {
            data object Pick : GalleryGrid

            data class Set(val grid: Grid) : GalleryGrid
        }

        data object Donate : Action

        data object OnBoarding : Action
    }

    sealed interface LaunchUrl : SettingsIntent {
        data object OpenPolicy : LaunchUrl

        data object OpenSourceCode : LaunchUrl

        data object OpenProjectWebsite : LaunchUrl

        data object OpenDeveloperWebsite : LaunchUrl

        data object OpenLicense : LaunchUrl

        data object OpenTelegramCommunity : LaunchUrl

        data object OpenDiscordCommunity : LaunchUrl
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

    data object DismissDialog : SettingsIntent

    data class Drawer(val intent: DrawerIntent) : SettingsIntent
}
