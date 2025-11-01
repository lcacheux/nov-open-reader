package net.cacheux.nvplib

import net.cacheux.bytonio.utils.writer
import net.cacheux.nvplib.data.Apdu
import net.cacheux.nvplib.data.Apdu.Companion.PRST
import net.cacheux.nvplib.data.ArgumentsSimple
import net.cacheux.nvplib.data.Configuration
import net.cacheux.nvplib.data.ConfirmedAction
import net.cacheux.nvplib.data.ConfirmedAction.Companion.STORE_HANDLE
import net.cacheux.nvplib.data.DataApdu
import net.cacheux.nvplib.data.DataApdu.Companion.CONFIRMED_ACTION
import net.cacheux.nvplib.data.DataApdu.Companion.MDC_ACT_SEG_GET_INFO
import net.cacheux.nvplib.data.DataApdu.Companion.MDC_ACT_SEG_TRIG_XFER
import net.cacheux.nvplib.data.DataApdu.Companion.SCONFIRMED_EVENT_REPORT_CHOSEN
import net.cacheux.nvplib.data.EventReport.Companion.MDC_NOTI_CONFIG
import net.cacheux.nvplib.data.EventReport.Companion.MDC_NOTI_SEGMENT_DATA
import net.cacheux.nvplib.data.EventRequest
import net.cacheux.nvplib.utils.putUnsignedByte
import net.cacheux.nvplib.utils.putUnsignedShort

fun applicationSelect() =
    createTranceivePayload(NvpController.BY_NAME, 0, NvpController.NDEF_TAG_APPLICATION_SELECT, true)

fun capabilityContainerSelect() =
    createTranceivePayload(0, NvpController.FIRST_ONLY, NvpController.CAPABILITY_CONTAINER_SELECT)

fun ndefSelect() =
    createTranceivePayload(0, NvpController.FIRST_ONLY, NvpController.NDEF_SELECT)

fun createReadPayload(offset: Int, length: Int): ByteArray {
    return ByteArray(5).writer().apply {
        putUnsignedByte(NvpController.CLA)
        putUnsignedByte(NvpController.INS_RB)
        putUnsignedShort(offset)
        putUnsignedByte(length)
    }.byteArray
}

private fun createTranceivePayload(p1: Int, p2: Int, data: ByteArray, le: Boolean = false): ByteArray {
    return ByteArray(data.size + if (le) 6 else 5).writer().apply {
        putUnsignedByte(NvpController.CLA)
        putUnsignedByte(NvpController.INS_SL)
        putUnsignedByte(p1)
        putUnsignedByte(p2)
        putUnsignedByte(data.size)
        writeByteArray(data)
        if (le) {
            putUnsignedByte(0x00)
        }
    }.byteArray
}

/**
 * Following methods generate [Apdu] packets to be sent to the pen.
 */

fun retrieveInformation(invokeId: Int, config: Configuration) =
    Apdu(
        at = PRST,
        payload = DataApdu(
            invokeId = invokeId,
            dchoice = 0x0201,
            payload = EventRequest(
                handle = 0,
                currentTime = 0,
                type = MDC_NOTI_CONFIG,
                data = ByteArray(4).writer().apply {
                    putUnsignedShort(config.id)
                    putUnsignedShort(0)
                }.byteArray
            )
        )
    )

fun askInformation(invokeId: Int, config: Configuration) =
    Apdu(
        at = PRST,
        payload = DataApdu(
            invokeId = invokeId,
            dchoice = 0x0103,
            payload = ArgumentsSimple(handle = config.handle)
        )
    )

fun confirmedAction(invokeId: Int) =
    Apdu(
        at = PRST,
        payload = DataApdu(
            invokeId = invokeId,
            dchoice = CONFIRMED_ACTION,
            payload = ConfirmedAction.allSegment(
                handle = STORE_HANDLE,
                type = MDC_ACT_SEG_GET_INFO
            )
        )
    )

fun xferAction(invokeId: Int, segment: Int) =
    Apdu(
        at = PRST,
        payload = DataApdu(
            invokeId = invokeId,
            dchoice = CONFIRMED_ACTION,
            payload = ConfirmedAction.segment(
                handle = STORE_HANDLE,
                type = MDC_ACT_SEG_TRIG_XFER,
                segment = segment
            )
        )
    )

fun confirmedXfer(invokeId: Int, data: ByteArray) =
    Apdu(
        at = PRST,
        payload = DataApdu(
            invokeId = invokeId,
            dchoice = SCONFIRMED_EVENT_REPORT_CHOSEN,
            payload = EventRequest(
                handle = STORE_HANDLE,
                currentTime = -1,
                type = MDC_NOTI_SEGMENT_DATA,
                data = data
            )
        )
    )

fun eventRequestData(instance: Int, index: Int, count: Int, confirmed: Boolean): ByteArray =
    ByteArray(12).writer().apply {
        putUnsignedShort(instance)
        putUnsignedShort(0)
        putUnsignedShort(index)
        putUnsignedShort(0)
        putUnsignedShort(count)
        putUnsignedByte(0x00) // block
        putUnsignedByte(if (confirmed) 0x80 else 0)
    }.byteArray