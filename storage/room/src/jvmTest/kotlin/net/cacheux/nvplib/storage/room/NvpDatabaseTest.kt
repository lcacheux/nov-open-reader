package net.cacheux.nvplib.storage.room

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import net.cacheux.nvplib.storage.room.entities.RoomDose
import net.cacheux.nvplib.storage.room.entities.RoomPen
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class NvpDatabaseTest {
    @Test
    fun testNvpDatabase() = runBlocking {
        val database = getDatabase("test")
        database.doseDao().deleteAll()

        database.doseDao().let { dao ->
            dao.insertDose(RoomDose(time = 1, value = 2))
            assertEquals(1, dao.getAllDoses().first().size)
        }
    }

    @Test
    fun testBothTables() = runBlocking {
        val database = getDatabase("test2")
        database.doseDao().deleteAllPens()
        database.doseDao().deleteAllDoses()

        database.doseDao().let { dao ->
            val pen1 = dao.insertPen(RoomPen(serial = "ABCD1234", model = "Novopen 6"))
            val pen2 = dao.insertPen(RoomPen(serial = "ABCD5678", model = "Novopen Echo+"))
            assertEquals(2, dao.listAllPens().first().size)

            dao.insertDose(RoomDose(time = 10000, value = 2, pen = pen1))
            dao.insertDose(RoomDose(time = 2, value = 4, pen = pen1))
            dao.insertDose(RoomDose(time = 3, value = 6, pen = pen1))
            dao.insertDose(RoomDose(time = 3, value = 10, pen = pen2))
            dao.insertDose(RoomDose(time = 4, value = 10, pen = pen2))

            assertEquals(3, dao.getPenWithDoses("ABCD1234").first().doses.size)
            assertEquals(2, dao.getPenWithDoses("ABCD5678").first().doses.size)
        }
    }

    private fun getDatabase(name: String) = Room.databaseBuilder<NvpDatabase>(
        name = File(System.getProperty("java.io.tmpdir"), "$name.db").absolutePath,
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
