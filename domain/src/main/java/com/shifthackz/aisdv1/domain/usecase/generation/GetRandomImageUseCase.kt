package com.shifthackz.aisdv1.domain.usecase.generation

import android.graphics.Bitmap
import io.reactivex.rxjava3.core.Single

interface GetRandomImageUseCase {
    operator fun invoke(): Single<Bitmap>
}
