package com.shifthackz.aisdv1.domain.preference

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersToken
import com.shifthackz.aisdv1.domain.entity.Grid
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.Settings
import io.reactivex.rxjava3.core.Flowable

interface PreferenceManager {
    var automatic1111ServerUrl: String
    var swarmUiServerUrl: String
    var swarmUiModel: String
    var demoMode: Boolean
    var developerMode: Boolean
    var localDiffusionAllowCancel: Boolean
    var localDiffusionSchedulerThread: SchedulersToken
    var monitorConnectivity: Boolean
    var autoSaveAiResults: Boolean
    var saveToMediaStore: Boolean
    var formAdvancedOptionsAlwaysShow: Boolean
    var formPromptTaggedInput: Boolean
    var source: ServerSource
    var sdModel: String
    var hordeApiKey: String
    var openAiApiKey: String
    var huggingFaceApiKey: String
    var huggingFaceModel: String
    var stabilityAiApiKey: String
    var stabilityAiEngineId: String
    var forceSetupAfterUpdate: Boolean
    var localModelId: String
    var localUseNNAPI: Boolean
    var designUseSystemColorPalette: Boolean
    var designUseSystemDarkTheme: Boolean
    var designDarkTheme: Boolean
    var designColorToken: String
    var designDarkThemeToken: String
    var backgroundGeneration: Boolean
    var backgroundProcessCount: Int
    var galleryGrid: Grid

    fun observe(): Flowable<Settings>
}
