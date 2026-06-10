package com.shifthackz.aisdv1.presentation.utils

import android.content.Context
import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.log.FileLoggingTree
import com.shifthackz.aisdv1.core.sharing.shareEmail
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

/**
 * Coordinates `ReportProblemEmailComposer` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
class ReportProblemEmailComposer : KoinComponent {

    /**
     * Exposes the `fileProviderDescriptor` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val fileProviderDescriptor: FileProviderDescriptor by inject()
    /**
     * Exposes the `buildInfoProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val buildInfoProvider: BuildInfoProvider by inject()

    /**
     * Executes the `invoke` step in the SDAI presentation layer.
     *
     * @param context Android context used by the operation.
     * @author Dmitriy Moroz
     */
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
