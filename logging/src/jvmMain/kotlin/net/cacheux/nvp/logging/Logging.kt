package net.cacheux.nvp.logging

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun logDebug(message: () -> String) {
    val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    println("$timestamp: ${message()}")
}
