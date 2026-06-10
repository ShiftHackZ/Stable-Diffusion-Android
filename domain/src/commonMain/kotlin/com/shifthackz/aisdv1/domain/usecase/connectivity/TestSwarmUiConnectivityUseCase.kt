package com.shifthackz.aisdv1.domain.usecase.connectivity

interface TestSwarmUiConnectivityUseCase {
    suspend operator fun invoke(url: String)
}
