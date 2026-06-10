package com.shifthackz.aisdv1.domain.entity

data class HordeProcessStatus(
    val waitTimeSeconds: Int,
    val queuePosition: Int?,
)
