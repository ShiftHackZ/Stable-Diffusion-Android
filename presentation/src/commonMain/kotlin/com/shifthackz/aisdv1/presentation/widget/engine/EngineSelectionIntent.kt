package com.shifthackz.aisdv1.presentation.widget.engine

import com.shifthackz.aisdv1.core.mvi.MviIntent

/**
 * Carries `EngineSelectionIntent` data through the SDAI presentation layer.
 *
 * @param value value value consumed by the API.
 * @author Dmitriy Moroz
 */
data class EngineSelectionIntent(val value: String) : MviIntent
