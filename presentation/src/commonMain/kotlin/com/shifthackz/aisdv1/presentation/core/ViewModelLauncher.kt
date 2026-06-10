package com.shifthackz.aisdv1.presentation.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job

/**
 * Aliases `ViewModelLauncher` for SDAI presentation code.
 *
 * @author Dmitriy Moroz
 */
internal typealias ViewModelLauncher = (
    CoroutineDispatcher,
    CoroutineStart,
    suspend CoroutineScope.() -> Unit,
) -> Job
