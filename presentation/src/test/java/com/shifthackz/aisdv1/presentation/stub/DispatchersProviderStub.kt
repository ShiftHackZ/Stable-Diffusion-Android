package com.shifthackz.aisdv1.presentation.stub

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

val stubDispatchersProvider = object : DispatchersProvider {
    override val io: CoroutineDispatcher = Dispatchers.Default
    override val ui: CoroutineDispatcher = Dispatchers.Default
    override val immediate: CoroutineDispatcher = Dispatchers.Default
}
