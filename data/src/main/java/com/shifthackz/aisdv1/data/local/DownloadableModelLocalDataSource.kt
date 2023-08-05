package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import io.reactivex.rxjava3.core.Single
import java.io.File

internal class DownloadableModelLocalDataSource(
    private val fileProviderDescriptor: FileProviderDescriptor,
) : DownloadableModelDataSource.Local {

    override fun exists(): Single<Boolean> = Single.create { emitter ->
        try {
            val dir = File(fileProviderDescriptor.localModelDirPath)
            val files = (dir.listFiles()?.filter { it.isDirectory }) ?: emptyList<File>()
            debugLog("--------------------------------------------")
            debugLog("DIR : ${dir.path}")
            debugLog("LS  : \n${files.map { "F(${it.path})\n" }}")
            debugLog("--------------------------------------------")
            emitter.onSuccess(dir.exists() && files.size == 4)
        } catch (e: Exception) {
            emitter.onSuccess(false)
        }
    }
}
