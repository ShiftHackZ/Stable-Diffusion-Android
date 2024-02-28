package com.shifthackz.aisdv1.domain.feature.diffusion

import android.graphics.Bitmap
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface LocalDiffusion {
    fun process(payload: TextToImagePayload): Single<Bitmap>
    fun interrupt(): Completable
    fun observeStatus(): Observable<Status>

    data class Status(val current: Int, val total: Int)
}
