package com.solaisc.notemark.util

import java.time.Instant
import java.time.Year
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun String.toDateString(): String {
    val instant = Instant.parse(this)

    val currentYear = Year.now(ZoneId.systemDefault()).value
    val noteYear = instant.atZone(ZoneId.systemDefault()).toLocalDate().year

    val formatterWithoutYear = DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault())
        .withZone(ZoneId.systemDefault())
    val formatterWithYear = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
        .withZone(ZoneId.systemDefault())

    return if (currentYear == noteYear) { formatterWithoutYear.format(instant) } else formatterWithYear.format(instant)
}

fun String.toDateTimeString(): String {
    val instant = Instant.parse(this)

    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.getDefault())
        .withZone(ZoneId.systemDefault())

    return formatter.format(instant)
}