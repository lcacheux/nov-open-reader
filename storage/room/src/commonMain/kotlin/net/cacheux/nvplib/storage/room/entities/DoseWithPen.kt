package net.cacheux.nvplib.storage.room.entities

import androidx.room.Embedded
import androidx.room.Relation
import net.cacheux.nvp.model.Dose

data class DoseWithPen(
    @Embedded val dose: RoomDose,
    @Relation(
        parentColumn = "pen",
        entityColumn = "id"
    )
    val pen: RoomPen
) {
    fun toDose() = Dose(
        time = dose.time,
        value = dose.value,
        serial = pen.serial,
        color = pen.color
    )
}
