package com.shifthackz.aisdv1.network.qualifiers

fun interface HordeApiKeyProvider {
    operator fun invoke(): String
}
