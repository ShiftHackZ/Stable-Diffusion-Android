@file:Suppress("NOTHING_TO_INLINE")

package com.shifthackz.aisdv1.core.common.log

import timber.log.Timber

inline fun debugLog(tag: String, message: String) {
    Timber.tag(tag).d(message)
}

inline fun debugLog(tag: String, message: Any?) {
    Timber.tag(tag).d(message.toString())
}

inline fun infoLog(tag: String, message: String? = null, error: Throwable? = null) {
    Timber.tag(tag).i(error, message)
}

inline fun errorLog(tag: String, error: Throwable? = null, message: String? = null) {
    Timber.tag(tag).e(error, message)
}

// region generic extensions
inline fun <reified T : Any> T.debugLog(message: String) {
    debugLog(loggingTag, message)
}

inline fun <reified T : Any> T.debugLog(message: Any) {
    debugLog(loggingTag, message.toString())
}

inline fun <reified T : Any> T.infoLog(message: String? = null, error: Throwable? = null) {
    infoLog(loggingTag, message, error)
}

inline fun <reified T : Any> T.errorLog(error: Throwable? = null, message: String? = null) {
    errorLog(loggingTag, error, message)
}
// endregion

@PublishedApi
internal inline val <T : Any> T.loggingTag: String
    get() {
        val tag = this::class.java.name.substringAfterLast(".")
        if (tag.contains("$")) {
            return tag.substringBefore("$")
        }
        return tag
    }
