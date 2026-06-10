package com.shifthackz.aisdv1.data.preference

import com.shifthackz.aisdv1.core.common.extensions.shouldUseNewMediaStore

/**
 * Executes the `defaultSaveToMediaStore` step in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal actual fun defaultSaveToMediaStore(): Boolean = shouldUseNewMediaStore()
