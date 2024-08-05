package com.shifthackz.aisdv1.domain.usecase.donate

import com.shifthackz.aisdv1.domain.entity.Supporter
import io.reactivex.rxjava3.core.Single

interface FetchAndGetSupportersUseCase {
    operator fun invoke(): Single<List<Supporter>>
}
