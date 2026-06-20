@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.settings.model

import androidx.compose.material3.ExperimentalMaterial3Api
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.model.shortDisplayName

/**
 * Resolves a localization key to the UiText format consumed by Settings rows.
 *
 * @param key localization key from the app string catalog.
 * @author Dmitriy Moroz
 */
internal fun text(key: String): UiText = Localization.string(key).asUiText()

/**
 * Maps server source enum values to compact labels used as Settings row end values.
 *
 * @author Dmitriy Moroz
 */
internal fun ServerSource.shortTitle(): String = shortDisplayName()
