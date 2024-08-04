package com.shifthackz.aisdv1.domain.datasource

import io.reactivex.rxjava3.core.Single

interface SwarmUiSessionDataSource {
    fun getSessionId(connectUrl: String? = null): Single<String>
}
