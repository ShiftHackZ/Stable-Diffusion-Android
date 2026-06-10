@file:Suppress("NOTHING_TO_INLINE")

package com.shifthackz.aisdv1.core.common.log

import timber.log.Timber

/**
 * Executes the `debugLog` step in the SDAI core common layer.
 *
 * @param tag tag value consumed by the API.
 * @param message message value consumed by the API.
 * @author Dmitriy Moroz
 */
inline fun debugLog(tag: String, message: String) {
    Timber.tag(tag).d(message)
}

/**
 * Executes the `debugLog` step in the SDAI core common layer.
 *
 * @param tag tag value consumed by the API.
 * @param message message value consumed by the API.
 * @author Dmitriy Moroz
 */
inline fun debugLog(tag: String, message: Any?) {
    Timber.tag(tag).d(message.toString())
}

/**
 * Executes the `infoLog` step in the SDAI core common layer.
 *
 * @param tag tag value consumed by the API.
 * @param message message value consumed by the API.
 * @param error error value consumed by the API.
 * @author Dmitriy Moroz
 */
inline fun infoLog(tag: String, message: String? = null, error: Throwable? = null) {
    Timber.tag(tag).i(error, message)
}

/**
 * Executes the `errorLog` step in the SDAI core common layer.
 *
 * @param tag tag value consumed by the API.
 * @param error error value consumed by the API.
 * @param message message value consumed by the API.
 * @author Dmitriy Moroz
 */
inline fun errorLog(tag: String, error: Throwable? = null, message: String? = null) {
    Timber.tag(tag).e(error, message)
}

// region generic extensions
/**
 * Executes the `debugLog` step in the SDAI core common layer.
 *
 * @param message message value consumed by the API.
 * @author Dmitriy Moroz
 */
inline fun <reified T : Any> T.debugLog(message: String) {
    debugLog(loggingTag, message)
}

/**
 * Executes the `debugLog` step in the SDAI core common layer.
 *
 * @param message message value consumed by the API.
 * @author Dmitriy Moroz
 */
inline fun <reified T : Any> T.debugLog(message: Any) {
    debugLog(loggingTag, message.toString())
}

/**
 * Executes the `infoLog` step in the SDAI core common layer.
 *
 * @param message message value consumed by the API.
 * @param error error value consumed by the API.
 * @author Dmitriy Moroz
 */
inline fun <reified T : Any> T.infoLog(message: String? = null, error: Throwable? = null) {
    infoLog(loggingTag, message, error)
}

/**
 * Executes the `errorLog` step in the SDAI core common layer.
 *
 * @param error error value consumed by the API.
 * @param message message value consumed by the API.
 * @author Dmitriy Moroz
 */
inline fun <reified T : Any> T.errorLog(error: Throwable? = null, message: String? = null) {
    errorLog(loggingTag, error, message)
}
// endregion

/**
 * Exposes the `property` value used by the SDAI core common layer.
 *
 * @author Dmitriy Moroz
 */
@PublishedApi
internal inline val <T : Any> T.loggingTag: String
    get() {
        val tag = this::class.java.name.substringAfterLast(".")
        if (tag.contains("$")) {
            return tag.substringBefore("$")
        }
        return tag
    }
