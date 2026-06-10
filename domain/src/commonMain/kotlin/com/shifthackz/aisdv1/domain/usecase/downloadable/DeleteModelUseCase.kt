package com.shifthackz.aisdv1.domain.usecase.downloadable

interface DeleteModelUseCase {
    suspend operator fun invoke(id: String)
}
