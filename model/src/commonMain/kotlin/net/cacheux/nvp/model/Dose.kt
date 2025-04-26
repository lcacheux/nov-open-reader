package net.cacheux.nvp.model

data class Dose(
    val time: Long,
    val value: Int,
    val ignored: Boolean = false,
    val serial: String = "",
    val color: String = ""
): DatedItem {
    fun ignored() = Dose(time, value, true, serial, color)

    fun displayedValue() = String.format("%.1f", value.toFloat() / 10)

    /**
     * Compare with another without the ignored field
     */
    fun compareValues(other: Dose) =
        time == other.time && value == other.value && serial == other.serial

    override fun date() = timestampToDate(time)
}
