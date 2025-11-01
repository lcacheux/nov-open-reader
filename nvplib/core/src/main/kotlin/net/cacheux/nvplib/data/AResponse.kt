package net.cacheux.nvplib.data

import net.cacheux.bytonio.BinarySerializable
import net.cacheux.bytonio.annotations.DataObject
import net.cacheux.bytonio.annotations.EncodeAsShort
import net.cacheux.nvplib.data.ApoepElement.Companion.SYS_TYPE_MANAGER
import net.cacheux.nvplib.generated.AResponseSerializer

@DataObject
data class AResponse(
    @EncodeAsShort var result: Int,
    @EncodeAsShort var protocol: Int,
    var apoep: ApoepElement
): BinarySerializable {
    override fun getBinarySize() = AResponseSerializer.getBinarySize(this)
    override fun toByteArray(): ByteArray {
        apoep.recMode = 0
        apoep.configId = 0
        apoep.systemType = SYS_TYPE_MANAGER.toInt()
        apoep.listCount = 0
        apoep.listLen = 0

        return AResponseSerializer.toByteArray(this)
    }
}
