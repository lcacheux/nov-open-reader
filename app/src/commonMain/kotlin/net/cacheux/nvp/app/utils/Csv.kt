package net.cacheux.nvp.app.utils

import net.cacheux.nvp.model.Dose
import java.text.SimpleDateFormat
import java.util.Date

fun List<Dose>.toCsv(): String {
    val builder = StringBuilder("Serial;Timestamp;Time;Value\n")
    forEach {
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(it.time)
        builder.append("${it.serial};${it.time};$date;${it.value}\n")
    }
    return builder.toString()
}

fun String.csvToDoseList(): List<Dose> {
    return split("\n").mapNotNull {
        try {
            it.csvLineToDose()
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}

@Throws(IllegalArgumentException::class)
fun String.csvLineToDose(): Dose {
    return with (split(";")) {
        if (size != 4) throw IllegalArgumentException("Incorrect CSV line: not enough fields")
        try {
            Dose(
                time = get(1).toLong(),
                value = get(3).toInt(),
                serial = get(0)
            )
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Incorrect CSV line: wrong number format")
        }
    }
}

fun csvFilename(serial: String?): String {
    val date = SimpleDateFormat("YYYYMMDD_HHmm").format(Date(System.currentTimeMillis()))
    return serial?.let {
        "nvp_export_${it}_$date"
    } ?: "nvp_export_all_$date"
}
