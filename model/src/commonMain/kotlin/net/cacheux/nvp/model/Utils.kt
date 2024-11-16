package net.cacheux.nvp.model

import java.time.Instant
import java.time.ZoneId

fun timestampToDate(timestamp: Long): Long {
    val instant = Instant.ofEpochMilli(timestamp)
    val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
    return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
}
