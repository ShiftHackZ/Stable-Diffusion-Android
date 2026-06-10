package com.shifthackz.aisdv1.core.common.schedulers

import kotlinx.coroutines.Dispatchers

object DefaultDispatchersProvider : DispatchersProvider {
    override val io = Dispatchers.Default
    override val ui = Dispatchers.Main
    override val immediate = Dispatchers.Main.immediate
}
