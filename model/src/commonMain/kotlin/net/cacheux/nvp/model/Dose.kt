package net.cacheux.nvp.model

data class Dose(
    val time: Long,
    val value: Int,
    val ignored: Boolean = false,
    val serial: String = ""
): DatedItem {
    fun ignored() = Dose(time, value, true, serial)

    fun displayedValue() = String.format("%.1f", InsulinUnit(value).toFloat())

    /**
     * Compare with another without the ignored field
     */
    fun compareValues(other: Dose) =
        time == other.time && value == other.value && serial == other.serial

    override fun date() = timestampToDate(time)
}
