package com.shifthackz.aisdv1.domain.usecase.generation

interface GetRandomImageUseCase {
    suspend operator fun invoke(): ByteArray
}
