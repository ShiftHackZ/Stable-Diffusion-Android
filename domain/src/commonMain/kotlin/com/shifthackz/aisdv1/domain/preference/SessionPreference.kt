package com.shifthackz.aisdv1.domain.preference

/**
 * Defines the `SessionPreference` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface SessionPreference {
    /**
     * Exposes the `swarmUiSessionId` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    var swarmUiSessionId: String
}
