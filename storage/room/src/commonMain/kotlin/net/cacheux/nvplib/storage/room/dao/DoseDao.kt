package net.cacheux.nvplib.storage.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import net.cacheux.nvplib.storage.room.entities.DoseWithPen
import net.cacheux.nvplib.storage.room.entities.PenWithDoses
import net.cacheux.nvplib.storage.room.entities.RoomDose
import net.cacheux.nvplib.storage.room.entities.RoomPen

@Dao
interface DoseDao {
    @Insert
    suspend fun insertDose(dose: RoomDose): Long

    @Query("SELECT * FROM dose ORDER BY time DESC")
    fun getAllDoses(): Flow<List<RoomDose>>

    @Transaction
    @Query("SELECT * FROM dose ORDER BY time DESC")
    fun getAllDosesWithPen(): Flow<List<DoseWithPen>>

    @Transaction
    @Query("SELECT * FROM dose, pen WHERE pen.serial = :serial AND dose.pen = pen.id ORDER BY time DESC LIMIT 1")
    fun getLastDose(serial: String): Flow<DoseWithPen?>

    @Insert
    suspend fun insertPen(pen: RoomPen): Long

    @Update
    suspend fun updatePen(pen: RoomPen)

    @Query("SELECT * FROM pen")
    fun listAllPens(): Flow<List<RoomPen>>

    @Query("SELECT * FROM pen WHERE serial = :serial")
    fun getPen(serial: String): Flow<RoomPen?>

    @Transaction
    @Query("SELECT * FROM pen WHERE serial = :serial")
    fun getPenWithDoses(serial: String): Flow<PenWithDoses>

    @Query("DELETE FROM dose WHERE pen = (SELECT id FROM pen WHERE serial = :serial)")
    suspend fun deleteDosesByPenSerial(serial: String)

    @Query("DELETE FROM pen WHERE serial = :serial")
    suspend fun deletePenBySerial(serial: String)

    @Query("DELETE FROM dose WHERE id = :id")
    suspend fun deleteDoseById(id: Long)

    @Query("DELETE FROM dose")
    suspend fun deleteAllDoses()

    @Query("DELETE FROM pen")
    suspend fun deleteAllPens()

    suspend fun deletePen(serial: String) {
        deleteDosesByPenSerial(serial)
        deletePenBySerial(serial)
    }

    suspend fun deleteAll() {
        deleteAllPens()
        deleteAllDoses()
    }
}
