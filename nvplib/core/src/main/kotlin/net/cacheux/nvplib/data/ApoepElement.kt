package net.cacheux.nvplib.data

import net.cacheux.nvplib.annotations.IsInt
import net.cacheux.nvplib.annotations.IsShort
import net.cacheux.nvplib.utils.getByteArray
import net.cacheux.nvplib.utils.getUnsignedInt
import net.cacheux.nvplib.utils.getUnsignedShort
import java.nio.ByteBuffer

data class ApoepElement(
    @IsInt var version: Int,
    @IsShort var encoding: Int,
    @IsInt var nomenclature: Int,
    @IsInt var functional: Int,
    @IsInt var systemType: Int,
    var systemId: ByteArray,
    @IsShort var configId: Int,
    @IsInt var recMode: Int,
    @IsShort var listCount: Int,
    @IsShort var listLen: Int,
): Encodable() {
    companion object {
        const val APOEP = 20601
        const val SYS_TYPE_MANAGER = 0x80000000
        const val SYS_TYPE_AGENT = 0x00800000

        fun fromByteBuffer(buffer: ByteBuffer) = ApoepElement(
            version = buffer.getUnsignedInt(),
            encoding = buffer.getUnsignedShort(),
            nomenclature = buffer.getUnsignedInt(),
            functional = buffer.getUnsignedInt(),
            systemType = buffer.getUnsignedInt(),
            systemId = buffer.getByteArray(),
            configId = buffer.getUnsignedShort(),
            recMode = buffer.getUnsignedInt(),
            listCount = buffer.getUnsignedShort(),
            listLen = buffer.getUnsignedShort(),
        )
    }
}
