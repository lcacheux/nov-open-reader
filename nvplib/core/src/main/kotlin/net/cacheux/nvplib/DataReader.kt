package net.cacheux.nvplib

import java.nio.ByteBuffer
import kotlin.experimental.and

/**
 * Base interface for input/output communication used by [NvpController].
 */
interface DataReader {
    /**
     * Override to implement the mechanism to send input then read the result.
     */
    fun readData(input: ByteArray): ByteArray

    /**
     * Override to be notified of each packet sent to pen.
     */
    fun onDataSent(data: ByteArray) {}

    /**
     * Override to be notified of each packet received from pen.
     */
    fun onDataReceived(data: ByteArray) {}
}

fun DataReader.readResult(command: ByteArray): TransceiveResult {
    onDataSent(command)
    val data = readData(command)
    onDataReceived(data)
    val buffer = ByteBuffer.wrap(data)
    val dataSize = buffer.remaining() - 2
    val result = ByteArray(dataSize)
    buffer.get(result, 0, dataSize)

    return TransceiveResult(
        content = ByteBuffer.wrap(result),
        success = (buffer.getShort() and 0xffff.toShort()) == NvpController.COMMAND_COMPLETED
    )
}
