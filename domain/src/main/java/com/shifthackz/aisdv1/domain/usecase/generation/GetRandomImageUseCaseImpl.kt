package com.shifthackz.aisdv1.domain.usecase.generation

import android.graphics.Bitmap
import com.shifthackz.aisdv1.domain.repository.RandomImageRepository
import io.reactivex.rxjava3.core.Single

class GetRandomImageUseCaseImpl(
    private val randomImageRepository: RandomImageRepository,
) : GetRandomImageUseCase {

    override fun invoke(): Single<Bitmap> = randomImageRepository.fetchAndGet()
}
