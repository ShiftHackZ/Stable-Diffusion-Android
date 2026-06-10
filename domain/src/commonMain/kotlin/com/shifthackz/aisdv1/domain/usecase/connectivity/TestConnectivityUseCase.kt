package com.shifthackz.aisdv1.domain.usecase.connectivity

interface TestConnectivityUseCase {
    suspend operator fun invoke(url: String)
}
