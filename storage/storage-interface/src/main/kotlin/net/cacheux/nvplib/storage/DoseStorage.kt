package net.cacheux.nvplib.storage

import kotlinx.coroutines.flow.Flow
import net.cacheux.nvp.model.Dose
import net.cacheux.nvp.model.PenInfos

interface DoseStorage {

    suspend fun addDose(dose: Dose, pen: PenInfos)

    fun getAllDoses(serial: String? = null): Flow<List<Dose>>

    fun getLastDose(serial: String): Flow<Dose?>

    suspend fun addPen(pen: PenInfos)

    suspend fun updatePen(pen: PenInfos)

    suspend fun deletePen(serial: String)

    suspend fun deleteDose(id: Long)

    fun listAllPens(): Flow<List<PenInfos>>
}
