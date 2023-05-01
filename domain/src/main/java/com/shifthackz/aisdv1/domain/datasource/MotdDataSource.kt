package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.Motd
import io.reactivex.rxjava3.core.Single

sealed interface MotdDataSource {

    interface Remote {
        fun fetch(): Single<Motd>
    }
}
