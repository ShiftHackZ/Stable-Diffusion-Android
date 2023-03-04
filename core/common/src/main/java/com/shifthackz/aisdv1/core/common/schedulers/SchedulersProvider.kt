package com.shifthackz.aisdv1.core.common.schedulers

import io.reactivex.rxjava3.core.Scheduler
import java.util.concurrent.Executor

interface SchedulersProvider {
    val io: Scheduler
    val ui: Scheduler
    val computation: Scheduler
    val singleThread: Executor
}
