package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.Supporter
import com.shifthackz.aisdv1.storage.db.persistent.entity.SupporterEntity

/**
 * Converts SDAI data with `mapDomainToEntity`.
 *
 * @return Result produced by `mapDomainToEntity`.
 * @author Dmitriy Moroz
 */
fun List<Supporter>.mapDomainToEntity(): List<SupporterEntity> =
    map(Supporter::mapDomainToEntity)

/**
 * Converts SDAI data with `mapDomainToEntity`.
 *
 * @author Dmitriy Moroz
 */
fun Supporter.mapDomainToEntity(): SupporterEntity = with(this) {
    SupporterEntity(
        id = id,
        name = name,
        date = date.toEpochMillis(),
        message = message,
    )
}

/**
 * Converts SDAI data with `mapEntityToDomain`.
 *
 * @return Result produced by `mapEntityToDomain`.
 * @author Dmitriy Moroz
 */
fun List<SupporterEntity>.mapEntityToDomain(): List<Supporter> =
    map(SupporterEntity::mapEntityToDomain)

/**
 * Converts SDAI data with `mapEntityToDomain`.
 *
 * @author Dmitriy Moroz
 */
fun SupporterEntity.mapEntityToDomain(): Supporter = with(this) {
    Supporter(
        id = id,
        name = name,
        date = date.toIsoDate(),
        message = message,
    )
}

/**
 * Exposes the `MILLIS_PER_DAY` value used by the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
private const val MILLIS_PER_DAY = 86_400_000L

/**
 * Converts SDAI data with `toEpochMillis`.
 *
 * @return Result produced by `toEpochMillis`.
 * @author Dmitriy Moroz
 */
private fun String.toEpochMillis(): Long = runCatching {
    val (year, month, day) = split("-").map(String::toInt)
    daysFromCivil(year, month, day) * MILLIS_PER_DAY
}.getOrDefault(0L)

/**
 * Converts SDAI data with `toIsoDate`.
 *
 * @return Result produced by `toIsoDate`.
 * @author Dmitriy Moroz
 */
private fun Long.toIsoDate(): String {
    val (year, month, day) = civilFromDays(floorDiv(this, MILLIS_PER_DAY))
    return "${year.toString().padStart(4, '0')}-${month.padded()}-${day.padded()}"
}

/**
 * Executes the `daysFromCivil` step in the SDAI data layer.
 *
 * @param year year value consumed by the API.
 * @param month month value consumed by the API.
 * @param day day value consumed by the API.
 * @return Result produced by `daysFromCivil`.
 * @author Dmitriy Moroz
 */
private fun daysFromCivil(year: Int, month: Int, day: Int): Long {
    var y = year.toLong()
    val m = month.toLong()
    y -= if (m <= 2) 1 else 0
    val era = floorDiv(y, 400)
    val yearOfEra = y - era * 400
    val dayOfYear = (153 * (m + if (m > 2) -3 else 9) + 2) / 5 + day - 1
    val dayOfEra = yearOfEra * 365 + yearOfEra / 4 - yearOfEra / 100 + dayOfYear
    return era * 146_097 + dayOfEra - 719_468
}

/**
 * Executes the `civilFromDays` step in the SDAI data layer.
 *
 * @param days days value consumed by the API.
 * @return Result produced by `civilFromDays`.
 * @author Dmitriy Moroz
 */
private fun civilFromDays(days: Long): Triple<Long, Long, Long> {
    val shiftedDays = days + 719_468
    val era = floorDiv(shiftedDays, 146_097)
    val dayOfEra = shiftedDays - era * 146_097
    val yearOfEra = (dayOfEra - dayOfEra / 1_460 + dayOfEra / 36_524 - dayOfEra / 146_096) / 365
    var year = yearOfEra + era * 400
    val dayOfYear = dayOfEra - (365 * yearOfEra + yearOfEra / 4 - yearOfEra / 100)
    val monthPrime = (5 * dayOfYear + 2) / 153
    val day = dayOfYear - (153 * monthPrime + 2) / 5 + 1
    val month = monthPrime + if (monthPrime < 10) 3 else -9
    year += if (month <= 2) 1 else 0
    return Triple(year, month, day)
}

/**
 * Executes the `floorDiv` step in the SDAI data layer.
 *
 * @param x x value consumed by the API.
 * @param y y value consumed by the API.
 * @return Result produced by `floorDiv`.
 * @author Dmitriy Moroz
 */
private fun floorDiv(x: Long, y: Long): Long {
    var result = x / y
    if ((x xor y) < 0 && result * y != x) result--
    return result
}

/**
 * Executes the `padded` step in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
private fun Long.padded(): String = toString().padStart(2, '0')
