package net.cacheux.nvplib.storage.room.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import net.cacheux.nvp.model.PenInfos

@Entity(
    tableName = "pen",
    indices = [ Index(value = ["serial"], unique = true) ]
)
data class RoomPen(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val serial: String,
    val model: String,
    val name: String = "",
    val color: String = ""
)

fun PenInfos.toRoomPen() = RoomPen(
    serial = serial,
    model = model,
    name = name,
    color = color
)

fun RoomPen.toPenInfos() = PenInfos(
    serial = serial,
    model = model,
    name = name,
    color = color
)
