package com.shifthackz.aisdv1.data.preference

import com.shifthackz.aisdv1.domain.preference.SessionPreference

class SessionPreferenceImpl : SessionPreference {

    private var _coinsPerDay: Int = -1

    override var coinsPerDay: Int
        get() = _coinsPerDay
        set(value) {
            _coinsPerDay = value
        }
}
