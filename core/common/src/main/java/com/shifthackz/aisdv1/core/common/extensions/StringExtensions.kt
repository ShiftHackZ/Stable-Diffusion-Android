package com.shifthackz.aisdv1.core.common.extensions

private const val PROTOCOL_DELIMITER = "://"
private const val PROTOCOL_HOLDER = "[[_PROTOCOL_]]"

fun String.withoutUrlProtocol(): String {
    if (!this.contains(PROTOCOL_DELIMITER)) return this
    val decomposed = this.split(PROTOCOL_DELIMITER)
    if (decomposed.size < 2) return this
    return decomposed.last()
}

fun String.fixUrlSlashes(): String = this
    .replace(PROTOCOL_DELIMITER, PROTOCOL_HOLDER)
    .replace(Regex("/{2,}"), "/")
    .let { str -> if (str.last() == '/') str.substring(0, str.lastIndex) else str }
    .replace(PROTOCOL_HOLDER, PROTOCOL_DELIMITER)
