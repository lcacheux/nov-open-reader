package net.cacheux.nvplib.utils

import net.cacheux.bytonio.utils.ByteArrayReader
import net.cacheux.bytonio.utils.ByteArrayWriter

fun ByteArrayWriter.putUnsignedByte(s: Int) {
    writeByte((s and 0xff).toByte())
}

fun ByteArrayWriter.putUnsignedShort(s: Int) {
    writeShort((s and 0xffff).toShort())
}

fun ByteArrayReader.getUnsignedShort(): Int {
    return readShort() and 0x0000ffff
}

fun ByteArrayReader.getUnsignedByte(): Int {
    return readByte().toInt() and 0x000000ff
}

fun ByteArrayReader.getIndexedString() =
    String(readByteArray(getUnsignedShort())).replace(
        "\u0000",
        ""
    )
