package net.cacheux.nvp.model

interface DatedItem {
    fun date(): Long
}

fun <T: DatedItem> List<T>.groupByDate()
    = this.groupBy { it.date() }
