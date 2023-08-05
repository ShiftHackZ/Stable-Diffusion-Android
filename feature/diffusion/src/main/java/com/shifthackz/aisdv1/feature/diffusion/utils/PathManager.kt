package com.shifthackz.aisdv1.feature.diffusion.utils

import android.content.Context

object PathManager {
    fun getModelPath(context: Context): String {
        return context.filesDir.absolutePath + "/model"
    }

    fun getCustomPath(context: Context): String {
        return context.filesDir.absolutePath + "/custom"
    }
}
