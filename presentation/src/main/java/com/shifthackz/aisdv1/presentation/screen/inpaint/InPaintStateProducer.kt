package com.shifthackz.aisdv1.presentation.screen.inpaint

import android.graphics.Bitmap
import com.shifthackz.aisdv1.presentation.model.InPaintModel
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.subjects.BehaviorSubject

class InPaintStateProducer {

    private val inPaintSubject: BehaviorSubject<InPaintModel> = BehaviorSubject.create()
    private val bitmapSubject: BehaviorSubject<Bitmap> = BehaviorSubject.create()

    fun observeInPaint() = inPaintSubject.toFlowable(BackpressureStrategy.LATEST)

    fun observeBitmap() = bitmapSubject.toFlowable(BackpressureStrategy.LATEST)

    fun updateInPaint(model: InPaintModel) = inPaintSubject.onNext(model)

    fun updateBitmap(bitmap: Bitmap) = bitmapSubject.onNext(bitmap)
}
