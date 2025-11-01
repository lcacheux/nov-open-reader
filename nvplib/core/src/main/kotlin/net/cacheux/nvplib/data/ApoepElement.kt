package net.cacheux.nvplib.data

import net.cacheux.bytonio.BinarySerializable
import net.cacheux.bytonio.annotations.DataObject
import net.cacheux.bytonio.annotations.EncodeAsShort
import net.cacheux.nvplib.generated.ApoepElementSerializer

@DataObject
data class ApoepElement(
    var version: Int,
    @EncodeAsShort var encoding: Int,
    var nomenclature: Int,
    var functional: Int,
    var systemType: Int,
    var systemId: ByteArray,
    @EncodeAsShort var configId: Int,
    var recMode: Int,
    @EncodeAsShort var listCount: Int,
    @EncodeAsShort var listLen: Int,
): BinarySerializable {
    companion object {
        const val APOEP = 20601
        const val SYS_TYPE_MANAGER = 0x80000000
        const val SYS_TYPE_AGENT = 0x00800000
    }

    override fun getBinarySize() = ApoepElementSerializer.getBinarySize(this)
    override fun toByteArray() = ApoepElementSerializer.toByteArray(this)
}
