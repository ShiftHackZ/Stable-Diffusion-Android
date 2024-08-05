package com.shifthackz.aisdv1.domain.entity

import java.util.Date

data class Supporter(
    val id: Int,
    val name: String,
    val date: Date,
    val message: String,
)
