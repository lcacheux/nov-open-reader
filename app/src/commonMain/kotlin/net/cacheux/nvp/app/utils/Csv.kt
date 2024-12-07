package net.cacheux.nvp.app.utils

import net.cacheux.nvp.model.Dose
import java.text.SimpleDateFormat

fun List<Dose>.toCsv(): String {
    val builder = StringBuilder("Serial;Timestamp;Time;Value\n")
    forEach {
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(it.time)
        builder.append("${it.serial};${it.time};$date;${it.value}\n")
    }
    return builder.toString()
}
