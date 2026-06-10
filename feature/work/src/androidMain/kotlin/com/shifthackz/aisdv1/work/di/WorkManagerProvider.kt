package com.shifthackz.aisdv1.work.di

import androidx.work.WorkManager

/**
 * Executes the `function` step in the SDAI background work feature layer.
 *
 * @author Dmitriy Moroz
 */
fun interface WorkManagerProvider {
    operator fun invoke(): WorkManager
}
