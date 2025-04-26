package net.cacheux.nvplib.storage.room.entities

import androidx.room.Embedded
import androidx.room.Relation
import net.cacheux.nvp.model.Dose

data class PenWithDoses(
    @Embedded val roomPen: RoomPen,
    @Relation(
        parentColumn = "id",
        entityColumn = "pen",
        entity = RoomDose::class
    )
    val doses: List<RoomDose>
) {
    // One-to-many relation can't be sorted with room so we must have a workaround to sort doses
    // by time
    fun reorderDesc() = PenWithDoses(roomPen, doses.sortedByDescending { it.time } )

    fun toDoseList() = doses.map {
        Dose(
            time = it.time,
            value = it.value,
            serial = roomPen.serial,
            color = roomPen.color
        )
    }
}
