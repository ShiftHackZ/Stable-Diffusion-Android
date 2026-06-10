package com.shifthackz.aisdv1.domain.usecase.gallery

interface ToggleImageVisibilityUseCase {
    suspend operator fun invoke(id: Long): Boolean
}
