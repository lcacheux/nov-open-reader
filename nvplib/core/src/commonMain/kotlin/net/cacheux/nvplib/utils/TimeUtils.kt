package net.cacheux.nvplib.utils

/**
 * Round a millis long value to the previous second. Ie 12345678L will become 12345000L.
 */
fun Long.roundToSecond() =
    (this / 1000) * 1000
