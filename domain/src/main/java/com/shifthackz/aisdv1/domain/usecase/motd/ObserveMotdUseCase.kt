package com.shifthackz.aisdv1.domain.usecase.motd

import com.shifthackz.aisdv1.domain.entity.Motd
import io.reactivex.rxjava3.core.Flowable

interface ObserveMotdUseCase {
    operator fun invoke(): Flowable<Motd>
}
