@file:Suppress("MemberVisibilityCanBePrivate")

package com.shifthackz.aisdv1.work.core

import android.content.Context
import androidx.annotation.MainThread
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.impl.utils.SynchronousExecutor
import androidx.work.impl.utils.futures.SettableFuture
import com.google.common.util.concurrent.ListenableFuture
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.Executor

/**
 * Implementation of RxWorker from androidx library that is adopted to use with RxJava 3.
 */
internal abstract class Rx3Worker(
    appContext: Context,
    workerParams: WorkerParameters,
) : ListenableWorker(appContext, workerParams) {

    private var singleFutureObserverAdapter: SingleFutureAdapter<Result>? = null

    override fun startWork(): ListenableFuture<Result> {
        singleFutureObserverAdapter = SingleFutureAdapter()
        return convert(singleFutureObserverAdapter!!, createWork())
    }

    protected val backgroundScheduler: Scheduler
        get() = Schedulers.from(backgroundExecutor)

    @MainThread
    abstract fun createWork(): Single<Result>

    fun setProgress(data: Data): Single<Void> {
        return Single.fromFuture(setProgressAsync(data))
    }

    fun setCompletableProgress(data: Data): Completable {
        return Completable.fromFuture(setProgressAsync(data))
    }

    override fun onStopped() {
        super.onStopped()
        val observer = singleFutureObserverAdapter
        if (observer != null) {
            observer.dispose()
            singleFutureObserverAdapter = null
        }
    }

    override fun getForegroundInfoAsync(): ListenableFuture<ForegroundInfo> {
        return convert(SingleFutureAdapter(), foregroundInfo)
    }

    val foregroundInfo: Single<ForegroundInfo>
        get() {
            val message = (
                    "Expedited WorkRequests require a RxWorker to provide an implementation for"
                            + " `getForegroundInfo()`")
            return Single.error(IllegalStateException(message))
        }

    fun setForeground(foregroundInfo: ForegroundInfo): Completable {
        return Completable.fromFuture(setForegroundAsync(foregroundInfo))
    }

    private fun <T : Any> convert(
        adapter: SingleFutureAdapter<T>,
        single: Single<T>
    ): ListenableFuture<T> {
        val scheduler = backgroundScheduler
        single.subscribeOn(scheduler) // observe on WM's private thread
            .observeOn(Schedulers.from(taskExecutor.serialTaskExecutor))
            .subscribe(adapter)
        return adapter.mFuture
    }

    /**
     * An observer that can observe a single and provide it as a [ListenableWorker].
     */
    internal class SingleFutureAdapter<T : Any> : SingleObserver<T>, Runnable {
        val mFuture: SettableFuture<T> = SettableFuture.create()
        private var mDisposable: Disposable? = null

        init {
            mFuture.addListener(this, INSTANT_EXECUTOR)
        }

        override fun onSubscribe(disposable: Disposable) {
            mDisposable = disposable
        }

        override fun onSuccess(t: T) {
            mFuture.set(t)
        }

        override fun onError(throwable: Throwable) {
            mFuture.setException(throwable)
        }

        override fun run() {
            if (mFuture.isCancelled) {
                dispose()
            }
        }

        fun dispose() {
            val disposable = mDisposable
            disposable?.dispose()
        }
    }

    companion object {
        val INSTANT_EXECUTOR: Executor = SynchronousExecutor()
    }
}
