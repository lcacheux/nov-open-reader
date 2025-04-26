package net.cacheux.nvp.app

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import net.cacheux.nvp.app.repository.StorageRepository
import net.cacheux.nvp.app.usecase.DoseListUseCase
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import java.util.Date

class DoseListUseCaseTest {

    private val mockStorageRepository = mock<StorageRepository> {
        on { getDoseList(any()) }.thenReturn(
            flowOf(
            genDoses(Date(2024, 1, 1, 12, 0, 0), 2,
                20, 60, 60)
                + genDoses(Date(2024, 1, 1, 13, 0, 0), 2,
                20, 120)
                + genDoses(Date(2024, 1, 1, 16, 0, 0), 2,
                300, 120)
            )
        )
    }

    @Test
    fun testGetDoseGroups() = runBlocking {
        val useCase = DoseListUseCase(
            storageRepository = mockStorageRepository,
            preferencesRepository = mockPreferencesRepository(
                groupEnabled = true,
                groupDelay = 60,
                autoIgnoreEnabled = true,
                autoIgnoreValue = 2
            )
        )

        with(useCase.getDoseGroups("any").first()) {
            assertEquals(3, size)
            assertEquals(120, get(0).getTotal())
            assertEquals(120, get(1).getTotal())
            assertEquals(420, get(2).getTotal())
        }
    }

    @Test
    fun testLongDelay() = runBlocking {
        val useCase = DoseListUseCase(
            storageRepository = mockStorageRepository,
            preferencesRepository = mockPreferencesRepository(
                groupEnabled = true,
                groupDelay = 60 * 61,
                autoIgnoreEnabled = true,
                autoIgnoreValue = 2
            )
        )

        with(useCase.getDoseGroups("any").first()) {
            assertEquals(2, size)
            assertEquals(260, get(0).getTotal())
            assertEquals(420, get(1).getTotal())
        }
    }

    @Test
    fun testNoAutoIgnore() = runBlocking {
        val useCase = DoseListUseCase(
            storageRepository = mockStorageRepository,
            preferencesRepository = mockPreferencesRepository(
                groupEnabled = true,
                groupDelay = 60,
                autoIgnoreEnabled = false
            )
        )

        with(useCase.getDoseGroups("any").first()) {
            assertEquals(3, size)
            assertEquals(140, get(0).getTotal())
            assertEquals(140, get(1).getTotal())
            assertEquals(420, get(2).getTotal())
        }
    }

    @Test
    fun testNoGrouping() = runBlocking {
        val useCase = DoseListUseCase(
            storageRepository = mockStorageRepository,
            preferencesRepository = mockPreferencesRepository(
                groupEnabled = false
            )
        )

        with(useCase.getDoseGroups("any").first()) {
            assertEquals(7, size)
            assertArrayEquals(
                intArrayOf(20, 60, 60, 20, 120, 300, 120),
                map { it.getTotal() }.toIntArray()
            )
        }
    }
}
