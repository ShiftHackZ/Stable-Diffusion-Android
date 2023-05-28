package com.shifthackz.aisdv1.domain.preference

import com.shifthackz.aisdv1.domain.entity.Settings
import io.reactivex.rxjava3.core.Flowable

interface PreferenceManager {
    var serverUrl: String
    var demoMode: Boolean
    var useSdAiCloud: Boolean
    var monitorConnectivity: Boolean
    var autoSaveAiResults: Boolean
    var formAdvancedOptionsAlwaysShow: Boolean

    fun observe(): Flowable<Settings>
}
