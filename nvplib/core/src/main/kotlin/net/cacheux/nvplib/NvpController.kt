package net.cacheux.nvplib

import net.cacheux.nvplib.data.ARequest
import net.cacheux.nvplib.data.AResponse
import net.cacheux.nvplib.data.Apdu
import net.cacheux.nvplib.data.ApoepElement.Companion.APOEP
import net.cacheux.nvplib.data.DataApdu
import net.cacheux.nvplib.data.EventReport
import net.cacheux.nvplib.data.FullSpecification
import net.cacheux.nvplib.data.InsulinDose
import net.cacheux.nvplib.data.PenResult
import net.cacheux.nvplib.data.PenResultData
import net.cacheux.nvplib.data.PhdPacket
import net.cacheux.nvplib.data.SegmentInfo
import net.cacheux.nvplib.data.SegmentInfoList
import net.cacheux.nvplib.data.T4Update
import net.cacheux.nvplib.data.dataApdu
import net.cacheux.nvplib.data.eventReport
import net.cacheux.nvplib.utils.wrap
import java.nio.ByteBuffer

/**
 * Implement the full pen reading protocol.
 *
 * @param dataReader An implementation of [DataReader] interface that will execute the input/output
 *                   process. Ie. can use NFC, socket, file...
 */
class NvpController(
    private val dataReader: DataReader
) {
    companion object {
        const val CLA = 0x00
        const val INS_SL = 0xA4
        const val INS_RB = 0xB0
        const val BY_NAME = 0x04
        const val FIRST_ONLY = 0x0C

        val NDEF_TAG_APPLICATION_SELECT = byteArrayOf(0xD2.toByte(), 0x76, 0x00, 0x00, 0x85.toByte(), 0x01, 0x01)
        val CAPABILITY_CONTAINER_SELECT = byteArrayOf(0xE1.toByte(), 0x03)
        val NDEF_SELECT = byteArrayOf(0xE1.toByte(), 0x04)

        const val COMMAND_COMPLETED = 0x9000.toShort()
    }

    private val phdManager = PhdManager(dataReader)

    fun dataRead(
        stopCondition: StopCondition = noStopCondition
    ): PenResult {
        dataReader.readResult(applicationSelect())
        dataReader.readResult(capabilityContainerSelect())
        dataReader.readResult(createReadPayload(0, 15))
        dataReader.readResult(ndefSelect())

        return retrieveConfiguration(stopCondition)
    }

    private fun retrieveConfiguration(
        stopCondition: StopCondition = noStopCondition
    ): PenResult {
        val lengthResult = dataReader.readResult(createReadPayload(0, 2))
        val length = lengthResult.content.getShort().toInt()

        val fullRead = dataReader.readResult(createReadPayload(2, length))

        val ack = T4Update(byteArrayOf(0xd0.toByte(), 0x00, 0x00))
        dataReader.readResult(ack.toByteArray())

        val phdPacket = PhdPacket.fromByteBuffer(fullRead.content)

        val apdu = Apdu.fromByteBuffer(ByteBuffer.wrap(phdPacket.content))

        val aRequest = apdu.payload as ARequest

        val sendApdu = Apdu(
            at = Apdu.AARE,
            payload = AResponse(
                result = 3, protocol = APOEP, apoep = aRequest.apoep
            )
        )

        val result = phdManager.sendApduRequest(sendApdu)
        val resultApdu = Apdu.fromByteBuffer(result.wrap())
        val dataApdu = (resultApdu.payload as DataApdu)
        val configuration = (dataApdu.payload as EventReport).configuration

        configuration?.let { config ->
            phdManager.sendApduRequest(retrieveInformation(dataApdu.invokeId, config))

            val info = phdManager.decodeDataApduRequest<FullSpecification>(askInformation(dataApdu.invokeId, config))

            val model = info.model.first()
            val serial = info.specification.serial
            val startTime = info.relativeTime
            val doseList = mutableListOf<InsulinDose>()

            val storageArray = phdManager.sendApduRequest(confirmedAction(dataApdu.invokeId))
            val storage = Apdu.fromByteBuffer(storageArray.wrap())

            (storage.payload as DataApdu).let { dataApdu ->
                (dataApdu.payload as SegmentInfoList).let { segmentInfoList ->
                    segmentInfoList.items.first().let {
                        readSegment(
                            it, dataApdu.invokeId, doseList,
                            stopCondition = { list ->
                                stopCondition(serial, list)
                            }
                        )

                        return PenResult.Success(
                            PenResultData(
                                model = model,
                                serial = serial,
                                startTime = startTime,
                                doseList = doseList
                            )
                        )
                    }
                }
            }
        }

        return PenResult.Failure("Unknown error")
    }

    private fun readSegment(
        segment: SegmentInfo, invokeId: Int, doseList: MutableList<InsulinDose>,
        stopCondition: (List<InsulinDose>) -> Boolean = { _ -> false}
    ) {
        val xferArray = phdManager.sendApduRequest(xferAction(
            invokeId = invokeId,
            segment = segment.instnum
        ))
        Apdu.fromByteBuffer(xferArray.wrap()) // xfer

        var result = phdManager.sendEmptyRequest()

        var currentInstance: Int
        var currentIndex: Int

        var finished = false

        do {
            if (result.isEmpty()) {
                result = phdManager.sendEmptyRequest()
            }

            val logApdu = Apdu.fromByteBuffer(result.wrap())
            logApdu.eventReport()?.let { eventReport ->
                doseList.addAll(eventReport.insulinDoses)

                if (eventReport.insulinDoses.isEmpty() or stopCondition(eventReport.insulinDoses))
                    finished = true

                currentInstance = eventReport.instance
                currentIndex = eventReport.index

                result = phdManager.sendApduRequest(confirmedXfer(
                    invokeId = logApdu.dataApdu()?.invokeId ?: 0,
                    data = eventRequestData(
                        currentInstance,
                        currentIndex,
                        eventReport.insulinDoses.size,
                        true)
                ))
            } ?: run { finished = true }
        } while (!finished)
    }
}
