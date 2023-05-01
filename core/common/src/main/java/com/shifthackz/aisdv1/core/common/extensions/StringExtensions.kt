package com.shifthackz.aisdv1.core.common.extensions

private const val PROTOCOL_DELIMITER = "://"

fun String.withoutUrlProtocol(): String {
    if (!this.contains(PROTOCOL_DELIMITER)) return this
    val decomposed = this.split(PROTOCOL_DELIMITER)
    if (decomposed.size < 2) return this
    return decomposed.last()
}
