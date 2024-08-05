package com.shifthackz.aisdv1.data.preference

import com.shifthackz.aisdv1.domain.preference.SessionPreference

class SessionPreferenceImpl : SessionPreference {

    private var _swarmUiSessionId: String = ""

    override var swarmUiSessionId: String
        get() = _swarmUiSessionId
        set(value) {
            _swarmUiSessionId = value
        }
}
