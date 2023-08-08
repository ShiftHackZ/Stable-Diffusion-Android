package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.io.File

internal class DownloadableModelLocalDataSource(
    private val fileProviderDescriptor: FileProviderDescriptor,
) : DownloadableModelDataSource.Local {

    private val localModelDirectory: File
        get() = File(fileProviderDescriptor.localModelDirPath)

    override fun exists(): Single<Boolean> = Single.create { emitter ->
        try {
            val files = (localModelDirectory.listFiles()?.filter { it.isDirectory }) ?: emptyList<File>()
            if (!emitter.isDisposed) emitter.onSuccess(localModelDirectory.exists() && files.size == 4)
        } catch (e: Exception) {
            if (!emitter.isDisposed) emitter.onSuccess(false)
        }
    }

    override fun delete(): Completable = Completable.fromAction {
        localModelDirectory.deleteRecursively()
    }
}
