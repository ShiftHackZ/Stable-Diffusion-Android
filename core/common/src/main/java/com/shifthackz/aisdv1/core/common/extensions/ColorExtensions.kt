package com.shifthackz.aisdv1.core.common.extensions

import android.graphics.Color
import kotlin.random.Random

fun randomColor() = Color.argb(
    255,
    Random.nextInt(256),
    Random.nextInt(256),
    Random.nextInt(256),
)
