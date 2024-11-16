package net.cacheux.nvp.model

data class Dose(
    val time: Long,
    val value: Int,
    val ignored: Boolean = false,
    val serial: String = ""
): DatedItem {
    fun ignored() = Dose(time, value, true, serial)

    fun displayedValue() = String.format("%.1f", value.toFloat() / 10)

    override fun date() = timestampToDate(time)
}
