package net.cacheux.nvplib.storage.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
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
    @Query("SELECT * FROM dose, pen WHERE pen.serial = :serial ORDER BY time DESC LIMIT 1")
    fun getLastDose(serial: String): Flow<DoseWithPen?>

    @Insert
    suspend fun insertPen(pen: RoomPen): Long

    @Query("SELECT * FROM pen")
    fun listAllPens(): Flow<List<RoomPen>>

    @Query("SELECT * FROM pen WHERE serial = :serial")
    fun getPen(serial: String): Flow<RoomPen?>

    @Transaction
    @Query("SELECT * FROM pen WHERE serial = :serial")
    fun getPenWithDoses(serial: String): Flow<PenWithDoses>

    @Query("DELETE FROM dose")
    suspend fun deleteAllDoses()

    @Query("DELETE FROM pen")
    suspend fun deleteAllPens()

    suspend fun deleteAll() {
        deleteAllPens()
        deleteAllDoses()
    }
}
