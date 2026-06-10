package com.shifthackz.aisdv1.core.common.appbuild

import android.content.Intent

/**
 * Executes the `function` step in the SDAI core common layer.
 *
 * @author Dmitriy Moroz
 */
fun interface ActivityIntentProvider {
    operator fun invoke(): Intent
}
