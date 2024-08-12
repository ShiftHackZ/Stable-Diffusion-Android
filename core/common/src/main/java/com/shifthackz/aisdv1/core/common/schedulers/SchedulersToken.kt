package com.shifthackz.aisdv1.core.common.schedulers

enum class SchedulersToken(val type: String) {
    MAIN_THREAD("Main thread"),
    IO_THREAD("IO thread"),
    COMPUTATION("Computation"),
    SINGLE_THREAD("Single thread"),
}
