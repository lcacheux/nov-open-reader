package net.cacheux.nvp.model

class DoseGroup(
    val doses: List<Dose>,
    val config: DoseGroupConfig = DoseGroupConfig()
): DatedItem {
    companion object {
        fun createDoseGroups(doses: List<Dose>, config: DoseGroupConfig = DoseGroupConfig()): List<DoseGroup> {
            val doseGroupList = mutableListOf<DoseGroup>()
            var currentList = mutableListOf<Dose>()
            doses.sortedBy { it.time }.forEachWithPrevious { previous, current ->
                if (
                    (previous != null && (previous.time + config.groupDelay * 1_000) < current.time)
                    or (previous != null && previous.serial != current.serial)
                ) {
                    if (currentList.isNotEmpty()) {
                        doseGroupList.add(DoseGroup(currentList.toDoseListWithIgnoredFlag(config), config))
                        currentList = mutableListOf()
                    }
                }
                currentList.add(current)
            }
            if (currentList.isNotEmpty())
                doseGroupList.add(
                    DoseGroup(currentList.toDoseListWithIgnoredFlag(config), config)
                )

            return doseGroupList
        }
    }

    fun getTime() = doses.last().time
    fun getSerial() = doses.last().serial
    fun getTotal() = doses.filter { !it.ignored }.sumOf { it.value }
    fun displayedTotal() = String.format("%.1f", getTotal().toFloat() / 10)

    override fun date() = timestampToDate(getTime())
}

fun List<Dose>.toDoseListWithIgnoredFlag(config: DoseGroupConfig): List<Dose> {
    val result = mutableListOf<Dose>()
    val i = iterator()
    var stillIgnore = true
    while (i.hasNext()) {
        val dose = i.next()
        if (stillIgnore && dose.value <= config.ignoreBelow && i.hasNext()) {
            result.add(dose.ignored())
        } else {
            result.add(dose)
            stillIgnore = false
        }
    }
    return result
}

fun <T> List<T>.forEachWithPrevious(action: (previous: T?, current: T) -> Unit) {
    var previous: T? = null
    for (item in this) {
        action(previous, item)
        previous = item
    }
}
