package net.cacheux.nvplib

import net.cacheux.nvplib.data.InsulinDose

typealias StopCondition = (String, List<InsulinDose>) -> Boolean

val noStopCondition: StopCondition = { _, _ -> false }

/**
 * Generate a [StopCondition] that will stop when reaching doses with time further specified value.
 * @param delayForSerial    One or more pairs of String/Long, with the pen serial number and the
 *                          latest value we want to retrieve.
 */
fun stopAfterDelay(vararg delayForSerial: Pair<String, Long>): StopCondition = { serial, list ->
    delayForSerial.firstOrNull { it.first == serial }?.let { pair ->
        list.find { it.time > pair.second }?.let { true } ?: false
    } ?: false
}
