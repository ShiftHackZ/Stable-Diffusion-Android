package com.shifthackz.aisdv1.presentation.activity

import com.shifthackz.aisdv1.core.viewmodel.RxViewModel
import com.shifthackz.aisdv1.domain.preference.PreferenceManager

class AiStableDiffusionViewModel(
    private val preferenceManager: PreferenceManager,
) : RxViewModel() {

    fun onStoragePermissionsGranted() {
        preferenceManager.saveToMediaStore = true
    }
}
