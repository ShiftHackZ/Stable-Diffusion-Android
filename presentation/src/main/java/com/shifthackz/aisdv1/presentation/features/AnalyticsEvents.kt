package com.shifthackz.aisdv1.presentation.features

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.feature.analytics.AnalyticsEvent
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailState
import com.shifthackz.aisdv1.presentation.screen.home.HomeNavigationItem

//region SETUP SCREEN
data class SetupConnectEvent(val url: String, val demoMode: Boolean) : AnalyticsEvent(
    name = "setup_connect_start",
    parameters = mapOf(
        "url" to url,
        "demo_mode" to "$demoMode",
    ),
)

data class SetupConnectFailure(val reason: String) : AnalyticsEvent(
    name = "setup_connect_failure",
    parameters = mapOf("reason" to reason),
)

object SetupConnectSuccess : AnalyticsEvent(name = "setup_connect_success")
//endregion

//region HOME SCREEN
data class HomeNavigationItemClick(val item: HomeNavigationItem) : AnalyticsEvent(
    name = "home_navigate_${item.name.lowercase()}",
)
//endregion

//region CONFIGURATION LOADER SCREEN
data class ConfigurationLoadFailure(val reason: String) : AnalyticsEvent(
    name = "configuration_load_failure",
    parameters = mapOf("reason" to reason),
)

object ConfigurationLoadSuccess : AnalyticsEvent(name = "configuration_load_success")
//endregion

//region SETTINGS SCREEN
object SettingsConfigurationClick : AnalyticsEvent(name = "settings_configuration_click")

object SettingsCacheCleared : AnalyticsEvent(name = "settings_cache_cleared")

object SettingsCheckUpdate : AnalyticsEvent(name = "settings_check_update")

object SettingsOpenMarket : AnalyticsEvent(name = "settings_open_market")

data class SdModelSelected(val value: String) : AnalyticsEvent(
    name = "settings_sd_model_selected",
    parameters = mapOf("model" to value),
)

data class MonitorConnectionChanged(val value: Boolean) : AnalyticsEvent(
    name = "settings_monitor_connection_change",
    parameters = mapOf("enabled" to "$value"),
)

data class AutoSaveAiResultsChanged(val value: Boolean) : AnalyticsEvent(
    name = "settings_auto_save_change",
    parameters = mapOf("enabled" to "$value"),
)
//endregion

//region GENERATION EVENTS
data class AiImageGenerated(val result: AiGenerationResult) : AnalyticsEvent(
    name = "${result.type.key}_generate_success",
    parameters = mapOf(
        "prompt" to result.prompt,
        "negative_prompt" to result.negativePrompt,
        "width" to result.width,
        "height" to result.height,
        "sampling_steps" to "${result.samplingSteps}",
        "cfg_scale" to "${result.cfgScale}",
        "restore_faces" to "${result.restoreFaces}",
        "sampler" to result.sampler,
        "seed" to result.seed,
    )
)
//endregion

//region GALLERY EVENTS
object GalleryGridItemClick : AnalyticsEvent(name = "gallery_grid_item_click")
object GalleryExportZip : AnalyticsEvent(name = "gallery_export_zip")

object GalleryItemImageShare : AnalyticsEvent(name = "gallery_item_image_share")
object GalleryItemInfoShare : AnalyticsEvent(name = "gallery_item_info_share")

object GalleryItemDelete : AnalyticsEvent("gallery_item_delete")

data class GalleryDetailTabClick(val tab :  GalleryDetailState.Tab):AnalyticsEvent(
    name = "gallery_detail_tab_${tab.toString().lowercase()}_click"
)
//endregion
