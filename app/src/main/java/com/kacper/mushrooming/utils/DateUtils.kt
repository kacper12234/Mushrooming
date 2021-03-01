package com.kacper.mushrooming.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

object DateUtils {
    fun formatDate(visitedAt: String): String {
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                .withLocale(Locale.getDefault())
                .withZone(ZoneId.systemDefault())
        return formatter.format(Instant.parse(visitedAt))
    }
}