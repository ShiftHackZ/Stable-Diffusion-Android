package com.shifthackz.aisdv1.presentation.screen.settings

import com.shifthackz.aisdv1.core.mvi.MviIntent
import com.shifthackz.aisdv1.domain.entity.ColorToken
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken
import com.shifthackz.aisdv1.domain.entity.Grid
import com.shifthackz.aisdv1.presentation.screen.drawer.DrawerIntent

/**
 * Defines the `SettingsIntent` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface SettingsIntent : MviIntent {

    /**
     * Provides the `NavigateConfiguration` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object NavigateConfiguration : SettingsIntent

    /**
     * Provides the `NavigateDeveloperMode` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object NavigateDeveloperMode : SettingsIntent

    /**
     * Defines the `SdModel` contract for the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    sealed interface SdModel : SettingsIntent {
        /**
         * Provides the `OpenChooser` singleton used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data object OpenChooser : SdModel

        /**
         * Carries `Select` data through the SDAI presentation layer.
         *
         * @param model model value consumed by the API.
         * @author Dmitriy Moroz
         */
        data class Select(val model: String) : SdModel
    }

    /**
     * Defines the `Action` contract for the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    sealed interface Action : SettingsIntent {
        /**
         * Coordinates `ClearAppCache` behavior in the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        enum class ClearAppCache : Action {
            Request, Confirm
        }

        /**
         * Provides the `ReportProblem` singleton used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data object ReportProblem : Action

        /**
         * Provides the `AppVersion` singleton used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data object AppVersion : Action

        /**
         * Provides the `PickLanguage` singleton used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data object PickLanguage : Action

        /**
         * Carries `SetLanguage` data through the SDAI presentation layer.
         *
         * @param languageCode BCP-47 language code handled by the platform layer.
         * @author Dmitriy Moroz
         */
        data class SetLanguage(val languageCode: String) : Action

        /**
         * Defines the `GalleryGrid` contract for the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        sealed interface GalleryGrid : Action {
            /**
             * Provides the `Pick` singleton used by the SDAI presentation layer.
             *
             * @author Dmitriy Moroz
             */
            data object Pick : GalleryGrid

            /**
             * Carries `Set` data through the SDAI presentation layer.
             *
             * @param grid grid value consumed by the API.
             * @author Dmitriy Moroz
             */
            data class Set(val grid: Grid) : GalleryGrid
        }

        /**
         * Provides the `Donate` singleton used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data object Donate : Action

        /**
         * Provides the `OnBoarding` singleton used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data object OnBoarding : Action
    }

    /**
     * Defines the `LaunchUrl` contract for the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    sealed interface LaunchUrl : SettingsIntent {
        /**
         * Provides the `OpenPolicy` singleton used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data object OpenPolicy : LaunchUrl

        /**
         * Provides the `OpenSourceCode` singleton used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data object OpenSourceCode : LaunchUrl

        /**
         * Provides the `OpenProjectWebsite` singleton used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data object OpenProjectWebsite : LaunchUrl

        /**
         * Provides the `OpenDeveloperWebsite` singleton used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data object OpenDeveloperWebsite : LaunchUrl

        /**
         * Provides the `OpenLicense` singleton used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data object OpenLicense : LaunchUrl

        /**
         * Provides the `OpenTelegramCommunity` singleton used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data object OpenTelegramCommunity : LaunchUrl

        /**
         * Provides the `OpenDiscordCommunity` singleton used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data object OpenDiscordCommunity : LaunchUrl
    }

    /**
     * Defines the `UpdateFlag` contract for the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    sealed interface UpdateFlag : SettingsIntent {
        /**
         * Exposes the `flag` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        val flag: Boolean

        /**
         * Carries `NNAPI` data through the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data class NNAPI(override val flag: Boolean) : UpdateFlag

        /**
         * Carries `MonitorConnection` data through the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data class MonitorConnection(override val flag: Boolean) : UpdateFlag

        /**
         * Carries `AutoSaveResult` data through the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data class AutoSaveResult(override val flag: Boolean) : UpdateFlag

        /**
         * Carries `BackgroundGeneration` data through the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data class BackgroundGeneration(override val flag: Boolean) : UpdateFlag

        /**
         * Carries `SaveToMediaStore` data through the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data class SaveToMediaStore(override val flag: Boolean) : UpdateFlag

        /**
         * Carries `TaggedInput` data through the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data class TaggedInput(override val flag: Boolean) : UpdateFlag

        /**
         * Carries `AdvancedFormVisibility` data through the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data class AdvancedFormVisibility(override val flag: Boolean) : UpdateFlag

        /**
         * Carries `DynamicColors` data through the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data class DynamicColors(override val flag: Boolean) : UpdateFlag

        /**
         * Carries `SystemDarkTheme` data through the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data class SystemDarkTheme(override val flag: Boolean) : UpdateFlag

        /**
         * Carries `DarkTheme` data through the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data class DarkTheme(override val flag: Boolean) : UpdateFlag
    }

    /**
     * Carries `NewColorToken` data through the SDAI presentation layer.
     *
     * @param token token value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class NewColorToken(val token: ColorToken) : SettingsIntent

    /**
     * Carries `NewDarkThemeToken` data through the SDAI presentation layer.
     *
     * @param token token value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class NewDarkThemeToken(val token: DarkThemeToken) : SettingsIntent

    /**
     * Provides the `DismissDialog` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object DismissDialog : SettingsIntent

    /**
     * Carries `Drawer` data through the SDAI presentation layer.
     *
     * @param intent intent to process in the MVI workflow.
     * @author Dmitriy Moroz
     */
    data class Drawer(val intent: DrawerIntent) : SettingsIntent
}
