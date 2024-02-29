package com.shifthackz.aisdv1.core.common.extensions

inline fun <T> T.applyIf(predicate: Boolean, block: T.() -> Unit): T {
    if (!predicate) return this
    return apply(block)
}
