package com.shifthackz.aisdv1.presentation.stub

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.Executor
import java.util.concurrent.Executors

val stubSchedulersProvider = object : SchedulersProvider {
    override val io: Scheduler = Schedulers.trampoline()
    override val ui: Scheduler = Schedulers.trampoline()
    override val computation: Scheduler = Schedulers.trampoline()
    override val singleThread: Executor = Executors.newSingleThreadExecutor()
}
