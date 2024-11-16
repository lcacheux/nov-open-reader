package net.cacheux.nvplib.data

import net.cacheux.nvplib.utils.hexToByteArray
import net.cacheux.nvplib.utils.putUnsignedShort
import net.cacheux.nvplib.utils.wrap
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.ByteBuffer

class DataApduTest {
    companion object {
        const val APDU_EVENT_REPORT = """
            D1 03 C9 50 48 44 82 E7 00 00 C4 00 C2 00 00 01 
            01 00 BC 00 00 00 00 00 00 0D 1C 00 B2 40 0A 00 
            04 00 AC 00 3D 01 00 00 08 00 38 0A 4D 00 02 08 
            00 09 43 00 02 00 00 09 41 00 04 00 00 03 20 09 
            53 00 02 00 00 0A 57 00 04 00 02 50 4D 09 51 00 
            02 00 01 0A 63 00 04 00 00 00 00 09 44 00 04 00 
            00 01 11 00 06 00 02 00 04 00 20 09 2F 00 04 00 
            82 34 01 0A 46 00 02 F0 40 09 96 00 02 15 60 0A 
            55 00 08 00 01 00 04 0A 56 00 04 00 05 00 03 00 
            03 00 1A 09 2F 00 04 00 82 34 02 0A 46 00 02 F0 
            40 0A 55 00 08 00 01 00 04 0A 66 00 02 00 06 00 
            04 00 03 00 1A 09 2F 00 04 00 82 F0 00 0A 46 00 
            02 F0 40 0A 55 00 08 00 01 00 04 0A 66 00 02 90 
            00
        """

        const val APDU_SPECIFICATION = """
            D1 03 D8 50 48 44 86 E7 00 00 D3 00 D1 00 00 02
            03 00 CB 00 00 00 08 00 C5 09 84 00 0A 00 08 00
            14 65 00 40 13 3E A8 09 8F 00 04 00 26 21 E4 0A
            45 00 10 20 00 1F 00 FF FF FF FF 00 00 1F 40 00
            00 00 00 09 2D 00 4B 00 04 00 47 00 01 00 01 00
            06 41 42 47 50 56 49 00 02 00 01 00 20 44 32 32
            34 35 31 37 32 39 36 30 30 30 30 30 20 44 32 32
            34 35 31 37 32 39 36 30 30 30 30 30 20 00 03 00
            01 00 01 00 00 04 00 01 00 08 30 31 2E 30 38 2E
            30 31 0A 5A 00 08 00 01 00 04 10 48 00 01 09 28
            00 1C 00 10 4E 6F 76 6F 20 4E 6F 72 64 69 73 6B
            20 41 2F 53 00 08 4E 6F 76 6F 50 65 6E 00 0A 44
            00 02 40 0A 0A 4B 00 16 00 02 00 12 02 01 00 08
            04 00 00 01 00 02 A0 48 02 02 00 02 00 00
        """

        const val APDU_SEGMENT_INFO = """
            D1 03 81 50 48 44 88 E7 00 00 7C 00 7A 00 00 02
            07 00 74 01 00 0C 0D 00 6E 00 01 00 6A 00 10 00
            06 00 64 09 22 00 02 00 10 0A 4E 00 36 40 00 00
            03 00 30 00 06 00 82 34 01 00 02 00 01 00 04 0A
            56 00 04 00 05 00 82 34 02 00 03 00 01 00 04 0A
            66 00 02 00 06 00 82 F0 00 00 04 00 01 00 04 0A
            66 00 02 09 53 00 02 00 00 0A 58 00 0A 00 08 44
            6F 73 65 20 4C 6F 67 09 7B 00 04 00 00 00 59 0A
            64 00 04 00 02 71 00
        """

        const val APDU_DOSE_LIST = """
            D1 03 FD 50 48 44 8E E7 00 00 F8 00 F6 80 01 01
            01 00 F0 01 00 00 B0 81 59 0D 21 00 E6 00 10 00
            00 00 00 00 00 00 12 80 00 00 D8 00 AF 3C BA FF
            00 01 A4 08 00 00 00 00 AF 3C A9 FF 00 01 A4 08
            00 00 00 00 AF 3C 96 FF 00 00 14 08 00 00 00 00
            AF 3C 95 FF 00 00 14 08 00 00 00 00 AF 3C 94 FF
            00 00 14 08 00 00 00 00 AF 3C 93 FF 00 00 14 08
            00 00 00 00 AD EA DD FF 00 01 A4 08 00 00 00 00
            AD EA D3 FF 00 01 A4 08 00 00 00 00 AD EA C8 FF
            00 00 14 08 00 00 00 00 AC A2 4A FF 00 01 A4 08
            00 00 00 00 AC A2 3B FF 00 01 A4 08 00 00 00 00
            AC A2 28 FF 00 00 14 08 00 00 00 00 AB 53 DB FF
            00 01 A4 08 00 00 00 00 AB 53 CF FF 00 01 A4 08
            00 00 00 00 AB 53 C1 FF 00 00 14 08 00 00 00 00
            AA 02 90 FF 00 01 AE 08 00 00 00 00 AA 02 7D FF
            00 00 14 08 00 00 00 00 AA 02 7C FF 00 00 14 08
            00 00 00
        """
    }

    @Test
    fun testDataApduEventReportParsing() {
        val phd = PhdPacket.fromByteBuffer(ByteBuffer.wrap(APDU_EVENT_REPORT.hexToByteArray()))
        val resultApdu = Apdu.fromByteBuffer(phd.content.wrap())

        assertEquals(0x0000e700, resultApdu.at)
        assertTrue(resultApdu.payload is DataApdu)

        (resultApdu.payload as DataApdu).let { data ->
            assertTrue(data.payload is EventReport)
            (data.payload as EventReport).let { eventReport ->
                assertNotNull(eventReport.configuration)
                assertEquals(16394, eventReport.configuration?.id)
                assertEquals(1, eventReport.configuration?.nbSegment)
                assertEquals(273, eventReport.configuration?.totalEntries)
                assertEquals(5472, eventReport.configuration?.unitCode)
                assertEquals(800, eventReport.configuration?.totalStorage)
            }
        }
    }

    @Test
    fun testSpecificationParsing() {
        val phd = PhdPacket.fromByteBuffer(ByteBuffer.wrap(APDU_SPECIFICATION.hexToByteArray()))
        val resultApdu = Apdu.fromByteBuffer(phd.content.wrap())

        assertEquals(0x0000e700, resultApdu.at)
        assertTrue(resultApdu.payload is DataApdu)

        (resultApdu.payload as DataApdu).let { data ->
            assertTrue(data.payload is FullSpecification)
            (data.payload as FullSpecification).let { fullSpec ->
                assertNotNull(fullSpec.specification)
                assertEquals("ABGPVI", fullSpec.specification.serial)
                assertEquals("01.08.01", fullSpec.specification.softwareRevision)
            }
        }
    }

    @Test
    fun testDataApduSegmentInfoParsing() {
        val phd = PhdPacket.fromByteBuffer(ByteBuffer.wrap(APDU_SEGMENT_INFO.hexToByteArray()))
        val resultApdu = Apdu.fromByteBuffer(phd.content.wrap())
        assertEquals(0x0000e700, resultApdu.at)
        assertTrue(resultApdu.payload is DataApdu)
        (resultApdu.payload as DataApdu).let { data ->
            assertTrue(data.payload is SegmentInfoList)
            (data.payload as SegmentInfoList).let { segmentInfoList ->
                assertEquals(1, segmentInfoList.items.size)
                assertEquals(6, segmentInfoList.items[0].items.size)
            }
        }
    }

    @Test
    fun testDataApduDoseListParsing() {
        val phd = PhdPacket.fromByteBuffer(ByteBuffer.wrap(APDU_DOSE_LIST.hexToByteArray()))
        val resultApdu = Apdu.fromByteBuffer(phd.content.wrap())
        assertEquals(0x0000e700, resultApdu.at)
        assertTrue(resultApdu.payload is DataApdu)
        (resultApdu.payload as DataApdu).let { data ->
            assertTrue(data.payload is EventReport)
            (data.payload as EventReport).let { eventReport ->
                assertEquals(18, eventReport.insulinDoses.size)
                assertEquals(420, eventReport.insulinDoses[0].units)
            }
        }
    }

    @Test
    fun testApduEncodingWithEventRequest() {
        val expectedResult = """
            E7 00 00 16 00 14 00 00 02 01 00 0E 00 00 00 00
            00 00 0D 1C 00 04 40 0A 00 00
        """.trimIndent()

        val eventRequest = EventRequest(
            handle = 0,
            currentTime = 0,
            type = EventReport.MDC_NOTI_CONFIG,
            data = ByteBuffer.allocate(4).apply {
                putUnsignedShort(16394)
                putUnsignedShort(0)
            }.array()
        )

        val dataApdu = DataApdu(
            invokeId = 0,
            dchoice = 0x0201,
            payload = eventRequest
        )

        val apdu = Apdu(
            at = Apdu.PRST,
            payload = dataApdu
        )

        assertArrayEquals(expectedResult.hexToByteArray(), apdu.toByteArray())
    }

    @Test
    fun testWithArgumentsSimple() {
        val expectedResult = """
            E7 00 00 0E 00 0C 00 01 01 03 00 06 00 37 00 00
            00 00
        """.trimIndent()

        val apdu = Apdu(
            at = Apdu.PRST,
            payload = DataApdu(
                invokeId = 1,
                dchoice = 0x0103,
                payload = ArgumentsSimple(handle = 55)
            )
        )

        assertArrayEquals(expectedResult.hexToByteArray(), apdu.toByteArray())
    }
}
