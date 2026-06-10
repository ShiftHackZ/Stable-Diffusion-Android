package com.shifthackz.aisdv1.domain.usecase.gallery

interface DeleteGalleryItemsUseCase {
    suspend operator fun invoke(ids: List<Long>)
}
