package net.cacheux.nvplib.utils

fun decomposeNumber(n: Int, maxValue: Int): List<Int> {
    val times = n / maxValue
    val remainder = n % maxValue
    val list = MutableList(times) { maxValue }
    if (remainder != 0) {
        list.add(remainder)
    }
    return list
}
