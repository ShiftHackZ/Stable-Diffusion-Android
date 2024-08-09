package com.shifthackz.aisdv1.core.common.appbuild

import android.content.Intent

fun interface ActivityIntentProvider {
    operator fun invoke(): Intent
}
