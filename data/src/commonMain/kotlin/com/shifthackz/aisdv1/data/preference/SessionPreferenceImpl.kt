package com.shifthackz.aisdv1.data.preference

import com.shifthackz.aisdv1.domain.preference.SessionPreference

/**
 * Implements `SessionPreference` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class SessionPreferenceImpl : SessionPreference {

    /**
     * Exposes the `swarmUiSessionId` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    override var swarmUiSessionId: String = ""
}
