package com.shifthackz.aisdv1.core.imageprocessing.contract

import io.reactivex.rxjava3.core.Single

interface RxImageProcessor<I : Any, O : Any> {
    operator fun invoke(input: I): Single<O>
}
