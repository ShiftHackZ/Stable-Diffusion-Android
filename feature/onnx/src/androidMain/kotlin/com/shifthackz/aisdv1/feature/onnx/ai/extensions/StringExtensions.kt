package com.shifthackz.aisdv1.feature.onnx.ai.extensions

/**
 * Executes the `halfCorner` step in the SDAI ONNX local diffusion feature layer.
 *
 * @return Result produced by `halfCorner`.
 * @author Dmitriy Moroz
 */
internal fun String.halfCorner(): String {
    var output = this
    val regs = arrayOf(
        "！", "，", "。", "；", "~", "《", "》", "（", "）", "？",
        "”", "｛", "｝", "“", "：", "【", "】", "”", "‘", "’", "!", ",",
        ".", ";", "`", "<", ">", "\\(", "\\)", "\\?", "'", "\\{", "}", "\"",
        ":", "\\{", "}", "\"", "\'", "\'"
    )
    for (i in 0 until (regs.size / 2)) {
        output = output.replace(regs[i].toRegex(), regs[i + regs.size / 2])
    }
    return output
}

/**
 * Converts SDAI data with `toArrays`.
 *
 * @return Result produced by `toArrays`.
 * @author Dmitriy Moroz
 */
internal fun String.toArrays(): Array<String?> {
    val codePoints = codePoints().toArray()
    val words = arrayOfNulls<String>(codePoints.size)
    for (i in codePoints.indices) {
        val code = codePoints[i]
        words[i] = String(Character.toChars(code))
    }
    return words
}
