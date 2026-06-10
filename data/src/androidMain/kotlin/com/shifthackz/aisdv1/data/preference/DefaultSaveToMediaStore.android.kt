package com.shifthackz.aisdv1.data.preference

import com.shifthackz.aisdv1.core.common.extensions.shouldUseNewMediaStore

internal actual fun defaultSaveToMediaStore(): Boolean = shouldUseNewMediaStore()
