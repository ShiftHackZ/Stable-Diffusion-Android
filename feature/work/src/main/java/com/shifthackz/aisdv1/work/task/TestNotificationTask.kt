package com.shifthackz.aisdv1.work.task

import android.content.Context
import androidx.work.WorkerParameters
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.notification.PushNotificationManager
import com.shifthackz.aisdv1.work.core.NotificationWorker
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.TimeUnit

internal class TestNotificationTask(
    context: Context,
    workerParameters: WorkerParameters,
    pushNotificationManager: PushNotificationManager,
) : NotificationWorker(context, workerParameters, pushNotificationManager) {

    override val notificationId: Int = 3336

    override val genericNotificationId: Int = 3337

    override fun createWork(): Single<Result> {
        return Observable
            .interval(1, TimeUnit.SECONDS)
            .take(10)
            .map {
//                setForegroundNotification("Progress", it.toInt() to 10)
                setForegroundNotification("Progress", "cc")
            }
            .toList()
            .map {
                gen("Generation complete!")
                Result.success()
            }
            .onErrorReturn { throwable ->
                errorLog(throwable)
                gen("Generation failed ;(")
                Result.failure()
            }
    }
}
