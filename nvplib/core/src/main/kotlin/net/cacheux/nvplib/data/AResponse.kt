package net.cacheux.nvplib.data

import net.cacheux.nvplib.annotations.IsShort
import net.cacheux.nvplib.data.ApoepElement.Companion.SYS_TYPE_MANAGER

data class AResponse(
    @IsShort var result: Int,
    @IsShort var protocol: Int,
    var apoep: ApoepElement
): Encodable() {
    override fun toByteArray(): ByteArray {
        apoep.recMode = 0
        apoep.configId = 0
        apoep.systemType = SYS_TYPE_MANAGER.toInt()
        apoep.listCount = 0
        apoep.listLen = 0

        return super.toByteArray()
    }
}
