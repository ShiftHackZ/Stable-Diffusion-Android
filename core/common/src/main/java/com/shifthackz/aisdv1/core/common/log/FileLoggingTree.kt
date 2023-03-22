package com.shifthackz.aisdv1.core.common.log

import android.util.Log
import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class FileLoggingTree : Timber.Tree(), KoinComponent {

    private val fileProviderDescriptor: FileProviderDescriptor by inject()
    private val schedulersProvider: SchedulersProvider by inject()
    private val buildInfoProvider: BuildInfoProvider by inject()

    private val formatDate: String
        get() = "[${SimpleDateFormat(LOGGER_TIMESTAMP_FORMAT, Locale.ROOT).format(Date())}]"

    private val formatPriority: (Int) -> String = {
        when (it) {
            Log.ASSERT -> "[A]"
            Log.DEBUG -> "[D]"
            Log.ERROR -> "[E]"
            Log.INFO -> "[I]"
            Log.VERBOSE -> "[V]"
            Log.WARN -> "[W]"
            else -> "[U]"
        }
    }

    private val formatTag: (String?) -> String = { tag ->
        tag?.let { "[$it]" } ?: LOGGER_DEFAULT_TAG
    }

    init {
        writeLine(buildString {
            appendLine("=== APP SESSION STARTED ===")
            appendLine()
            appendLine("Version : $buildInfoProvider")
            appendLine("Type    : ${buildInfoProvider.buildType}")
            appendLine()
        })
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val log = buildString {
            append(formatDate)
            append(formatPriority(priority))
            append(" ")
            append(formatTag(tag))
            append(" : ")
            append(message)
            t?.stackTraceToString()?.let { stacktrace ->
                appendLine()
                append(stacktrace)
            }
            appendLine()
        }
        writeLine(log)
    }

    private fun writeLine(message: String) = schedulersProvider.singleThread.execute {
        run {
            val cacheDirectory = File(fileProviderDescriptor.logsCacheDirPath)
            if (!cacheDirectory.exists()) cacheDirectory.mkdirs()
            val outFile = File(cacheDirectory, LOGGER_FILENAME)
            if (!outFile.exists()) outFile.createNewFile()
            FileOutputStream(outFile, true).use { fos ->
                val payload = message.toByteArray()
                fos.write(payload)
                fos.close()
            }
        }
    }

    companion object {
        private const val LOGGER_TIMESTAMP_FORMAT = "dd.MM.yyyy HH:mm:SS"
        private const val LOGGER_DEFAULT_TAG = "[SDAI]"
        private const val LOGGER_FILENAME = "sdaiv1.log"

        fun clearLog(fileProviderDescriptor: FileProviderDescriptor) {
            val cacheDirectory = File(fileProviderDescriptor.logsCacheDirPath)
            cacheDirectory.deleteRecursively()
        }
    }
}
