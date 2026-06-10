package com.shifthackz.aisdv1.presentation.screen.logger

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.log.FileLoggingTree
import java.io.File

class AndroidLogReader(
    private val fileProviderDescriptor: FileProviderDescriptor,
) : LogReader {
    override suspend fun read(): String {
        val logFile = File(
            fileProviderDescriptor.logsCacheDirPath,
            FileLoggingTree.LOGGER_FILENAME,
        )
        return logFile.readText()
    }
}
