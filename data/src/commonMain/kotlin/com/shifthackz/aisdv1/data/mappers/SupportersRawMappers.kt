package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.Supporter
import com.shifthackz.aisdv1.network.model.SupporterRaw

/**
 * Converts SDAI data with `mapRawToDomain`.
 *
 * @author Dmitriy Moroz
 */
fun List<SupporterRaw>.mapRawToDomain(): List<Supporter> = map(SupporterRaw::mapRawToDomain)

/**
 * Converts SDAI data with `mapRawToDomain`.
 *
 * @author Dmitriy Moroz
 */
fun SupporterRaw.mapRawToDomain(): Supporter = with(this) {
    Supporter(
        id = id ?: -1,
        name = name ?: "",
        date = date ?: "",
        message = message ?: "",
    )
}
