package net.cacheux.nvplib.storage.room

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import net.cacheux.nvp.model.Dose
import net.cacheux.nvp.model.PenInfos
import org.junit.Assert.assertEquals
import org.junit.Test

class RoomDoseStorageTest {
    @Test
    fun testListAllPens() = runBlocking {
        val storage = initStorage()

        storage.addPen(PenInfos("Novopen 6", "ABCD1234"))
        storage.addPen(PenInfos("Novopen 6", "ABCD456ABCD23457"))
        storage.addPen(PenInfos("Novopen Echo Plus", "ABCD3333"))

        with(storage.listAllPens().first()) {
            assertEquals(3, size)
            assertEquals("ABCD1234", this[0].serial)
            assertEquals("ABCD3333", this[2].serial)
        }
    }

    @Test
    fun testUpdatePen() = runBlocking {
        val storage = initStorage()

        storage.addPen(PenInfos("Novopen 6", "ABCD1234"))
        storage.addPen(PenInfos("Novopen 6", "EFGH5678"))

        with(storage.listAllPens().first()) {
            assertEquals(2, size)

            assertEquals("ABCD1234", this[0].serial)
            assertEquals("", this[0].name)
            assertEquals("", this[0].color)

            assertEquals("EFGH5678", this[1].serial)
            assertEquals("", this[1].name)
            assertEquals("", this[1].color)
        }

        storage.updatePen(PenInfos("Novopen 6", "ABCD1234", "First pen", "#eeeeee"))

        with(storage.listAllPens().first()) {
            assertEquals(2, size)

            assertEquals("ABCD1234", this[0].serial)
            assertEquals("First pen", this[0].name)
            assertEquals("#eeeeee", this[0].color)
        }

        storage.updatePen(PenInfos("Novopen 6", "EFGH5678", "Second pen", "#dddddd"))

        with(storage.listAllPens().first()) {
            assertEquals(2, size)

            assertEquals("EFGH5678", this[1].serial)
            assertEquals("Second pen", this[1].name)
            assertEquals("#dddddd", this[1].color)
        }
    }

    @Test
    fun testAddDose() = runBlocking {
        val storage = initStorage().apply { createDataset() }

        with(storage.listAllPens().first()) {
            assertEquals(2, size)
            assertEquals("ABCD1234", this[0].serial)
            assertEquals("ABCD2345", this[1].serial)
            assertEquals("Novopen Echo+", this[1].model)
        }

        with(storage.getAllDoses().first()) {
            assertEquals(5, size)
            assertEquals(37, this[0].value)
            assertEquals(42, this[3].value)
            assertEquals(2, this[4].value)
        }

        with(storage.getAllDoses("ABCD1234").first()) {
            assertEquals(3, size)
            assertEquals(11, this[0].value)
            assertEquals(2, this[2].value)
        }

        with(storage.getAllDoses("ABCD2345").first()) {
            assertEquals(2, size)
            assertEquals(37, this[0].value)
            assertEquals(2, this[1].value)
        }
    }

    @Test
    fun testGetLastDose() = runBlocking {
        val storage = initStorage().apply { createDataset() }

        assertEquals(12345700L, storage.getLastDose("ABCD1234").first()?.time)
        assertEquals(12346850L, storage.getLastDose("ABCD2345").first()?.time)
    }

    private fun initStorage(): RoomDoseStorage
        = RoomDoseStorage(
            Room.inMemoryDatabaseBuilder<NvpDatabase>()
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .build()
        )

    private suspend fun RoomDoseStorage.createDataset() {
        addDose(Dose(12345678L, 2), PenInfos("Novopen 6", "ABCD1234"))
        addDose(Dose(12345690L, 42), PenInfos("Novopen 6", "ABCD1234"))
        addDose(Dose(12345700L, 11), PenInfos("Novopen 6", "ABCD1234"))
        addDose(Dose(12346800L, 2), PenInfos("Novopen Echo+", "ABCD2345"))
        addDose(Dose(12346850L, 37), PenInfos("Novopen Echo+", "ABCD2345"))
    }
}
