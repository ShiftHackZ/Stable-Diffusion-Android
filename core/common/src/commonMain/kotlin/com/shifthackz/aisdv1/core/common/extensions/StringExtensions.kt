package com.shifthackz.aisdv1.core.common.extensions

/**
 * Exposes the `PROTOCOL_DELIMITER` value used by the SDAI core common layer.
 *
 * @author Dmitriy Moroz
 */
private const val PROTOCOL_DELIMITER = "://"
/**
 * Exposes the `PROTOCOL_HOLDER` value used by the SDAI core common layer.
 *
 * @author Dmitriy Moroz
 */
private const val PROTOCOL_HOLDER = "[[_PROTOCOL_]]"

/**
 * Executes the `fixUrlSlashes` step in the SDAI core common layer.
 *
 * @return Result produced by `fixUrlSlashes`.
 * @author Dmitriy Moroz
 */
fun String.fixUrlSlashes(): String = this
    .replace(PROTOCOL_DELIMITER, PROTOCOL_HOLDER)
    .replace(Regex("/{2,}"), "/")
    .let { str ->
        when {
            str.isEmpty() -> ""
            str.last() == '/' -> str.substring(0, str.lastIndex)
            else -> str
        }
    }
    .replace(PROTOCOL_HOLDER, PROTOCOL_DELIMITER)
