package net.cacheux.nvp.app

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import net.cacheux.nvp.app.utils.penInfos
import net.cacheux.nvp.model.Dose
import net.cacheux.nvp.model.PenInfos
import net.cacheux.nvplib.StopCondition
import net.cacheux.nvplib.data.PenResult
import net.cacheux.nvplib.stopAfterDelay
import net.cacheux.nvplib.storage.DoseStorage

class StorageRepository(
    private val storage: DoseStorage
): StopConditionProvider {
    fun getPenList() = storage.listAllPens()

    fun getDoseList(serial: String? = null): Flow<List<Dose>> = storage.getAllDoses(serial)

    override suspend fun getStopCondition(): StopCondition {
        val currentTime = System.currentTimeMillis()
        val stopMap = storage.listAllPens().first().map {
            //it.serial to (currentTime - storage.getAllDoses(it.serial).first().maxOf { it.time })
            it.serial to (currentTime - (storage.getLastDose(it.serial).first()?.time ?: 0))
        }

        return stopAfterDelay(
            *stopMap.toTypedArray()
        )
    }

    suspend fun saveDoseList(list: List<Dose>) {
        /**
         * A CSV export can have multiple time the same dose (if they all happened within the same
         * second), but we still want to ignore imported doses that are already in the database.
         * Because of this, we filter the list by removing any dose already in the database prior to
         * adding them.
         */
        val existing = storage.getAllDoses().first()
        list.filter { dose ->
            !existing.any { dose.compareValues(it) }
        }.forEach {
            storage.addDose(it, PenInfos(model = "", serial = it.serial))
        }
    }

    suspend fun saveResult(result: PenResult) {
        if (result is PenResult.Success) {
            val lastTime = storage.getLastDose(result.data.serial).first()?.time ?: 0L
            result.data.doseList.forEach { dose ->
                if (dose.time > lastTime)
                    storage.addDose(Dose(dose.time, dose.units), result.data.penInfos())
            }
        }
    }
}
