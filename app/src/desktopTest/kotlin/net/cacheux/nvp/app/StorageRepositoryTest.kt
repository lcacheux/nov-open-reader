package net.cacheux.nvp.app

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import net.cacheux.nvp.model.Dose
import net.cacheux.nvp.model.PenInfos
import net.cacheux.nvplib.data.InsulinDose
import net.cacheux.nvplib.data.PenResult
import net.cacheux.nvplib.data.PenResultData
import net.cacheux.nvplib.storage.DoseStorage
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class StorageRepositoryTest {

    @Test
    fun testSaveDoseList() = runBlocking {
        val mockDoseStorage = mock<DoseStorage> {
            on { getAllDoses() }.thenReturn(
                flowOf(
                    listOf(
                        Dose(12345678L, 2, serial = "ABCD"),
                        Dose(12345678L, 2, serial = "ABCD"),
                        Dose(12345688L, 2, serial = "ABCD"),
                        Dose(12345698L, 2, serial = "ABCD"),
                        Dose(12345678L, 2, serial = "CDEF"),
                        Dose(12345678L, 20, serial = "CDEF"),
                        Dose(12345688L, 20, serial = "CDEF"),
                        Dose(12345778L, 20, serial = "CDEF"),
                    )
                )
            )
        }

        val storageRepository = StorageRepository(mockDoseStorage)

        storageRepository.saveDoseList(listOf(
            Dose(12345678L, 2, serial = "ABCD"), // Duplicate
            Dose(12345678L, 30, serial = "ABCD"),
            Dose(12346666L, 10, serial = "ABCD"),
            Dose(12346666L, 10, serial = "ABCD"),
            Dose(12345678L, 2, serial = "CDEF"), // Duplicate
            Dose(12345778L, 20, serial = "CDEF"), // Duplicate
            Dose(22346666L, 40, serial = "CDEF"),
        ))

        verify(mockDoseStorage, times(4)).addDose(any(), any())
        verify(mockDoseStorage, times(3)).addDose(any(), eq(PenInfos(serial = "ABCD")))
        verify(mockDoseStorage, times(1)).addDose(any(), eq(PenInfos(serial = "CDEF")))
        verify(mockDoseStorage, times(2)).addDose(
            eq(Dose(12346666L, 10, serial = "ABCD")), eq(PenInfos(serial = "ABCD"))
        )
    }

    @Test
    fun testSaveResult() = runBlocking {
        val mockDoseStorage = mock<DoseStorage> {
            on { getLastDose(any()) }.thenReturn(flowOf(Dose(12345678L, 20, serial = "ABCD")))
        }

        val storageRepository = StorageRepository(mockDoseStorage)

        storageRepository.saveResult(PenResult.Success(
            PenResultData(
                model = "NovoPen", serial = "ABCD",
                startTime = 12345676L,
                doseList = listOf(
                    InsulinDose(12360908L, 70, 0),
                    InsulinDose(12360908L, 60, 0),
                    InsulinDose(12356008L, 50, 0),
                    InsulinDose(12355679L, 40, 0),
                    InsulinDose(12345679L, 20, 0), // Ignored
                    InsulinDose(12345676L, 10, 0), // Ignored
                )
            )
        ))

        verify(mockDoseStorage, times(4)).addDose(any(), any())
        verify(mockDoseStorage, times(1)).addDose(eq(Dose(12355679L, 40)), any())
        verify(mockDoseStorage, times(1)).addDose(eq(Dose(12356008L, 50)), any())
        verify(mockDoseStorage, times(1)).addDose(eq(Dose(12360908L, 60)), any())
        verify(mockDoseStorage, times(1)).addDose(eq(Dose(12360908L, 70)), any())
    }
}