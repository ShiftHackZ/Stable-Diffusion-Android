package com.shifthackz.aisdv1.presentation.features

import android.content.Context
import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.log.FileLoggingTree
import com.shifthackz.aisdv1.core.sharing.shareEmail
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

class ReportProblemEmailComposer : KoinComponent {

    private val fileProviderDescriptor: FileProviderDescriptor by inject()
    private val buildInfoProvider: BuildInfoProvider by inject()

    fun invoke(context: Context) {
        val logFile = File(
            fileProviderDescriptor.logsCacheDirPath +
                    "/" +
                    FileLoggingTree.LOGGER_FILENAME
        )
        context.shareEmail(
            email = "sdai@moroz.cc",
            subject = "SDAI - Problem report",
            body = "SDAI : $buildInfoProvider",
            file = if (!logFile.exists()) null else logFile,
            fileProviderPath = fileProviderDescriptor.providerPath,
        )
    }
}
