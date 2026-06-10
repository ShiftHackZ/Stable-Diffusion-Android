package com.shifthackz.aisdv1.domain.usecase.caching

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.log.FileLoggingTree

/**
 * Coordinates `AndroidAppCacheCleaner` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class AndroidAppCacheCleaner(
    /**
     * Exposes the `fileProviderDescriptor` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val fileProviderDescriptor: FileProviderDescriptor,
) : AppCacheCleaner {

    /**
     * Performs the SDAI side effect handled by `clear`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun clear() {
        FileLoggingTree.clearLog(fileProviderDescriptor)
    }
}
