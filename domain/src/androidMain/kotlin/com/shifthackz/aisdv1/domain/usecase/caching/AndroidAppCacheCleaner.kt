package com.shifthackz.aisdv1.domain.usecase.caching

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.log.FileLoggingTree

internal class AndroidAppCacheCleaner(
    private val fileProviderDescriptor: FileProviderDescriptor,
) : AppCacheCleaner {

    override suspend fun clear() {
        FileLoggingTree.clearLog(fileProviderDescriptor)
    }
}
