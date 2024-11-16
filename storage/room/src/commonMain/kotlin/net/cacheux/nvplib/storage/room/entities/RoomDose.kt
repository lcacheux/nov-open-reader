package net.cacheux.nvplib.storage.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import net.cacheux.nvp.model.Dose

@Entity(tableName = "dose")
data class RoomDose(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val time: Long,
    val value: Int,
    val pen: Long = 0,
)

fun Dose.toRoomDose(penId: Long = 0) = RoomDose(
    time = time,
    value = value,
    pen = penId
)

fun RoomDose.toDose() = Dose(
    time = time,
    value = value
)
