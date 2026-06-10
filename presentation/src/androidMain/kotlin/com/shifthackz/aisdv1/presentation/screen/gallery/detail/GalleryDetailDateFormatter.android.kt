package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import java.util.Date

internal actual fun formatGalleryCreatedAt(epochMillis: Long): String =
    Date(epochMillis).toString()
