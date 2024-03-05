package com.shifthackz.aisdv1.domain.preference

import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.Settings
import io.reactivex.rxjava3.core.Flowable

interface PreferenceManager {
    var serverUrl: String
    var demoMode: Boolean
    var monitorConnectivity: Boolean
    var autoSaveAiResults: Boolean
    var saveToMediaStore: Boolean
    var formAdvancedOptionsAlwaysShow: Boolean
    var formPromptTaggedInput: Boolean
    var source: ServerSource
    var hordeApiKey: String
    var openAiApiKey: String
    var huggingFaceApiKey: String
    var huggingFaceModel: String
    var forceSetupAfterUpdate: Boolean
    var localModelId: String
    var localUseNNAPI: Boolean
    var designUseSystemColorPalette: Boolean
    var designUseSystemDarkTheme: Boolean
    var designDarkTheme: Boolean
    var designColorToken: String

    fun observe(): Flowable<Settings>
}
