package com.shifthackz.aisdv1.presentation.theme

import android.os.Build
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

val dynamicColorAvailable: () -> Boolean = { Build.VERSION.SDK_INT >= Build.VERSION_CODES.S }

val LightColors = lightColorScheme()
val DarkColors = darkColorScheme()
