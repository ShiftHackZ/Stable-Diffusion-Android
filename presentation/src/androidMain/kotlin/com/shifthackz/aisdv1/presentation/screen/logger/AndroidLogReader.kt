package com.shifthackz.aisdv1.presentation.screen.logger

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.log.FileLoggingTree
import java.io.File

/**
 * Coordinates `AndroidLogReader` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
class AndroidLogReader(
    /**
     * Exposes the `fileProviderDescriptor` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val fileProviderDescriptor: FileProviderDescriptor,
) : LogReader {
    /**
     * Loads SDAI data through `read`.
     *
     * @return Result produced by `read`.
     * @author Dmitriy Moroz
     */
    override suspend fun read(): String {
        val logFile = File(
            fileProviderDescriptor.logsCacheDirPath,
            FileLoggingTree.LOGGER_FILENAME,
        )
        return logFile.readText()
    }
}
