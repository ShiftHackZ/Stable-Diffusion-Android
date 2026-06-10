package com.shifthackz.aisdv1.core.common.extensions

/**
 * Executes the `applyIf` step in the SDAI core common layer.
 *
 * @param predicate predicate value consumed by the API.
 * @param block block value consumed by the API.
 * @return Result produced by `applyIf`.
 * @author Dmitriy Moroz
 */
inline fun <T> T.applyIf(predicate: Boolean, block: T.() -> Unit): T {
    if (!predicate) return this
    return apply(block)
}
