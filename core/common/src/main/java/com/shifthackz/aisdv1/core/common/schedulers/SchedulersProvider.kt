package com.shifthackz.aisdv1.core.common.schedulers

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.Executor

interface SchedulersProvider {
    val io: Scheduler
    val ui: Scheduler
    val computation: Scheduler
    val singleThread: Executor

    fun byToken(token: SchedulersToken): Scheduler = when (token) {
        SchedulersToken.MAIN_THREAD -> ui
        SchedulersToken.IO_THREAD -> io
        SchedulersToken.COMPUTATION -> computation
        SchedulersToken.SINGLE_THREAD -> Schedulers.from(singleThread)
    }
}
