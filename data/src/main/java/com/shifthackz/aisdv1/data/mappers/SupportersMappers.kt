package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.core.common.extensions.toDate
import com.shifthackz.aisdv1.domain.entity.Supporter
import com.shifthackz.aisdv1.network.model.SupporterRaw
import com.shifthackz.aisdv1.storage.db.persistent.entity.SupporterEntity
import java.util.Date

//region RAW --> DOMAIN
fun List<SupporterRaw>.mapRawToDomain(): List<Supporter> = map(SupporterRaw::mapRawToDomain)

fun SupporterRaw.mapRawToDomain(): Supporter = with(this) {
    Supporter(
        id = id ?: -1,
        name = name ?: "",
        date = date?.toDate() ?: Date(),
        message = message ?: "",
    )
}
//endregion

//region DOMAIN --> ENTITY
fun List<Supporter>.mapDomainToEntity(): List<SupporterEntity> = map(Supporter::mapDomainToEntity)

fun Supporter.mapDomainToEntity(): SupporterEntity = with(this) {
    SupporterEntity(
        id = id,
        name = name,
        date = date,
        message = message,
    )
}
//endregion

//region ENTITY --> DOMAIN
fun List<SupporterEntity>.mapEntityToDomain(): List<Supporter> = map(SupporterEntity::mapEntityToDomain)

fun SupporterEntity.mapEntityToDomain(): Supporter = with(this) {
    Supporter(
        id = id,
        name = name,
        date = date,
        message = message,
    )
}
//endregion
