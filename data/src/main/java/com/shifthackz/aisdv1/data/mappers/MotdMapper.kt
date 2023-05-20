package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.Motd
import com.shifthackz.aisdv1.network.response.MotdResponse

fun MotdResponse.toDomain(): Motd = with(this) {
    Motd(
        display = display ?: false,
        title = title ?: "",
        subTitle = subTitle ?: "",
    )
}
