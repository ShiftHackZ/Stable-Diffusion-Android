package com.shifthackz.aisdv1.presentation.widget.source

import androidx.compose.runtime.Composable
import com.shifthackz.aisdv1.core.common.platform.Platform
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.model.displayName

/**
 * Resolves the localized provider label for composables that need a plain string.
 */
@Composable
fun ServerSource.getName(platform: Platform = Platform.ANDROID): String =
    getNameUiText(platform).asString()

/**
 * Resolves the provider label as [UiText] for non-composable consumers.
 */
fun ServerSource.getNameUiText(platform: Platform = Platform.ANDROID): UiText =
    displayName(platform).asUiText()
