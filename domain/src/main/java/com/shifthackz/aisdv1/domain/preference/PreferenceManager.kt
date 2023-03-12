package com.shifthackz.aisdv1.domain.preference

interface PreferenceManager {
    var serverUrl: String
    var demoMode: Boolean
    var monitorConnectivity: Boolean
    var autoSaveAiResults: Boolean
}
