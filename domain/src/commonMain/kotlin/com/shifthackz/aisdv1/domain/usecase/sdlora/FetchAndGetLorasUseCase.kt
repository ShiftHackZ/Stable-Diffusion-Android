package com.shifthackz.aisdv1.domain.usecase.sdlora

import com.shifthackz.aisdv1.domain.entity.LoRA

interface FetchAndGetLorasUseCase {
    suspend operator fun invoke(): List<LoRA>
}
