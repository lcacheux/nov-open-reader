package net.cacheux.nvplib.data

import net.cacheux.bytonio.BinarySerializable
import net.cacheux.bytonio.utils.byteArrayOf

/**
 * Base class for data classes that won't need serialization but still need BinarySerializable interface.
 */
open class DummyBinarySerializable: BinarySerializable {
    override fun getBinarySize() = 0
    override fun toByteArray() = byteArrayOf()
}