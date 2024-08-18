package com.shifthackz.aisdv1.core.extensions

import android.annotation.SuppressLint
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput

@SuppressLint("ReturnFromAwaitPointerEventScope")
fun Modifier.gesturesDisabled(disabled: Boolean = true) =
    if (disabled) {
        pointerInput(Unit) {
            awaitPointerEventScope {
                // we should wait for all new pointer events
                while (true) {
                    awaitPointerEvent(pass = PointerEventPass.Initial)
                        .changes
                        .forEach(PointerInputChange::consume)
                }
            }
        }
    } else {
        this
    }
