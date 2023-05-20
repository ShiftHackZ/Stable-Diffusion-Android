package com.shifthackz.aisdv1.domain.usecase.motd

import com.shifthackz.aisdv1.domain.entity.Motd
import com.shifthackz.aisdv1.domain.repository.MotdRepository
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

internal class ObserveMotdUseCaseImpl(
    private val motdRepository: MotdRepository,
) : ObserveMotdUseCase {

    private val motdProducer: (Long) -> Observable<Motd> = {
        motdRepository
            .fetchMotd()
            .onErrorReturn { motd }
            .doOnSuccess { motd = it }
            .toObservable()
    }

    private var motd: Motd = Motd()

    override operator fun invoke(): Flowable<Motd> {
        val refreshProducer: () -> Observable<Motd> = {
            Observable
                .interval(30L, TimeUnit.SECONDS)
                .flatMap(motdProducer::invoke)
        }

        val merged = Observable.merge(
            refreshProducer(),
            motdProducer(-1L),
        )

        return merged.toFlowable(BackpressureStrategy.LATEST)
    }
}
