package net.cacheux.nvp.app

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import net.cacheux.nvp.model.Dose
import net.cacheux.nvp.model.PenInfos
import net.cacheux.nvplib.storage.DoseStorage
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class StorageRepositoryTest {

    @Test
    fun saveDoseList() = runBlocking {
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
}