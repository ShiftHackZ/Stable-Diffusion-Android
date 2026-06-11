package com.shifthackz.aisdv1.presentation.screen.logger

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.shifthackz.aisdv1.core.sharing.shareText

/**
 * Implements `LoggerPlatformActions` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal class AndroidLoggerPlatformActions(
    /**
     * Exposes the `context` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val context: Context,
) : LoggerPlatformActions {
    /**
     * Executes the `copyLogs` step in the SDAI presentation layer.
     *
     * @param text text value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun copyLogs(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(CLIP_LABEL, text))
    }

    /**
     * Performs the SDAI side effect handled by `shareLogs`.
     *
     * @param text text value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun shareLogs(text: String) {
        context.shareText(text)
    }

    /**
     * Provides the `companion object` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private companion object {
        /**
         * Exposes the `CLIP_LABEL` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        const val CLIP_LABEL = "SDAI logs"
    }
}
