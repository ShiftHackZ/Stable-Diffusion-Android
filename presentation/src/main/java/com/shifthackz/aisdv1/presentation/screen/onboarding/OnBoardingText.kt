package com.shifthackz.aisdv1.presentation.screen.onboarding

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

private const val CHAR_BOLD_START = '['
private const val CHAR_BOLD_END = ']'

@Composable
fun buildOnBoardingText(@StringRes resId: Int): AnnotatedString =  buildAnnotatedString {
    val list = mutableListOf<Pair<String, FontWeight>>()
    val fullString = stringResource(id = resId)
    var currentSequence = ""
    var currentFontWeight = FontWeight.Light
    for (index in fullString.indices) {
        val char = fullString[index]
        fun add() {
            list.add(currentSequence to currentFontWeight)
            currentSequence = ""
        }
        if (char == CHAR_BOLD_START) {
            add()
            currentFontWeight = FontWeight.Bold
        } else if (char == CHAR_BOLD_END) {
            add()
            currentFontWeight = FontWeight.Light
        } else if (index == fullString.length - 1) {
            currentSequence += char
            add()
        }
        if (char == CHAR_BOLD_START || char == CHAR_BOLD_END) {
            continue
        }
        currentSequence += char
    }
    list.forEach {
        withStyle(style = SpanStyle(fontWeight = it.second)) {
            append(it.first)
        }
    }
}
