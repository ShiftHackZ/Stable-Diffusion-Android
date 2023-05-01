package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.Motd
import io.reactivex.rxjava3.core.Single

interface MotdRepository {
    fun fetchMotd(): Single<Motd>
}
