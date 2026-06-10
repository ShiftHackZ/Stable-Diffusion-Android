package com.shifthackz.aisdv1.domain.usecase.donate

import com.shifthackz.aisdv1.domain.entity.Supporter

fun interface FetchSupportersUseCase {

    suspend operator fun invoke(): List<Supporter>
}
