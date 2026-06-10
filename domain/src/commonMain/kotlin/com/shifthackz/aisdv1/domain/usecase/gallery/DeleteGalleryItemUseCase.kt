package com.shifthackz.aisdv1.domain.usecase.gallery

interface DeleteGalleryItemUseCase {
    suspend operator fun invoke(id: Long)
}
