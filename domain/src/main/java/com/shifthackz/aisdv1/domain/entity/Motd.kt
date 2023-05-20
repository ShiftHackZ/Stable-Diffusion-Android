package com.shifthackz.aisdv1.domain.entity

/**
 * Represents server message model.
 *
 * MOTD: Message Of The Day
 *
 * @param display defines if message should be visible
 * @param title string with message header
 * @param subTitle string with message body
 */
data class Motd(
    val display: Boolean = false,
    val title: String = "",
    val subTitle: String = "",
) {
    val isEmpty: Boolean
        get() = title.isEmpty() && subTitle.isEmpty()

    val isNotEmpty: Boolean
        get() = !isEmpty
}
