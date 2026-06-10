package com.shifthackz.aisdv1.core.common.schedulers

/**
 * Coordinates `SchedulersToken` behavior in the SDAI core common layer.
 *
 * @param type type value consumed by the API.
 * @author Dmitriy Moroz
 */
enum class SchedulersToken(val type: String) {
    MAIN_THREAD("Main thread"),
    IO_THREAD("IO thread"),
    COMPUTATION("Computation"),
    SINGLE_THREAD("Single thread"),
}
