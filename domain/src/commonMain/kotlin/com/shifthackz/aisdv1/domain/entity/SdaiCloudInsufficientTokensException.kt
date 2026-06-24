package com.shifthackz.aisdv1.domain.entity

class SdaiCloudInsufficientTokensException(
    message: String = "Not enough SDAI tokens.",
) : IllegalStateException(message)
