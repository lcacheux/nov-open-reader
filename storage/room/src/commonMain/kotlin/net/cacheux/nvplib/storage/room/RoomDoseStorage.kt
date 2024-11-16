package net.cacheux.nvplib.storage.room

import androidx.sqlite.SQLiteException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import net.cacheux.nvp.model.Dose
import net.cacheux.nvp.model.PenInfos
import net.cacheux.nvplib.storage.DoseStorage
import net.cacheux.nvplib.storage.room.entities.RoomDose
import net.cacheux.nvplib.storage.room.entities.toPenInfos
import net.cacheux.nvplib.storage.room.entities.toRoomPen

class RoomDoseStorage(
    private val database: NvpDatabase
): DoseStorage {

    override suspend fun addDose(
        dose: Dose,
        pen: PenInfos
    ) {
        val roomPenId = database.doseDao().getPen(pen.serial).first()?.id
            ?: database.doseDao().insertPen(pen.toRoomPen())
        try {
            database.doseDao().insertDose(
                RoomDose(
                    time = dose.time,
                    value = dose.value,
                    pen = roomPenId
                )
            )
        } catch (e: SQLiteException) {
            // Ignore duplicate inserts
        }
    }

    override fun getAllDoses(serial: String?): Flow<List<Dose>> {
        return serial?.let {
            database.doseDao().getPenWithDoses(it).map { pwd ->
                pwd.reorderDesc().toDoseList()
            }
        } ?: database.doseDao().getAllDosesWithPen().map {
            it.map { dose -> dose.toDose() }
        }
    }

    override fun getLastDose(serial: String): Flow<Dose?> {
        return database.doseDao().getLastDose(serial).map { it?.toDose() }
    }

    override suspend fun addPen(pen: PenInfos) {
        database.doseDao().insertPen(pen.toRoomPen())
    }

    override fun listAllPens(): Flow<List<PenInfos>>
        = database.doseDao().listAllPens().map { it.map { pen -> pen.toPenInfos() } }

    suspend fun deleteAll() {
        database.doseDao().deleteAll()
    }
}
