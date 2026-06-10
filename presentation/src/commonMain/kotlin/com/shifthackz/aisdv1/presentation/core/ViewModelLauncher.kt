package com.shifthackz.aisdv1.presentation.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job

internal typealias ViewModelLauncher = (
    CoroutineDispatcher,
    CoroutineStart,
    suspend CoroutineScope.() -> Unit,
) -> Job
