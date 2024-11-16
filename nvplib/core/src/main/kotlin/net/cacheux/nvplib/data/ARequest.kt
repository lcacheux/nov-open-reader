package net.cacheux.nvplib.data

import net.cacheux.nvplib.annotations.IsInt
import net.cacheux.nvplib.annotations.IsShort
import net.cacheux.nvplib.data.ApoepElement.Companion.APOEP
import net.cacheux.nvplib.utils.getByteArray
import net.cacheux.nvplib.utils.getUnsignedInt
import net.cacheux.nvplib.utils.getUnsignedShort
import java.nio.ByteBuffer

data class ARequest(
    @IsInt val protocol: Int,
    @IsInt val version: Int,
    @IsShort val elements: Int,
    val apoep: ApoepElement
): Encodable() {
    companion object {
        fun fromByteBuffer(data: ByteBuffer): ARequest {
            val version = data.getUnsignedInt()
            val elements = data.getUnsignedShort()
            data.getUnsignedShort() // len

            var apoep: ApoepElement? = null
            repeat(elements) {
                val protocol = data.getUnsignedShort()
                val bytes = data.getByteArray()
                if (protocol == APOEP) {
                    apoep = ApoepElement.fromByteBuffer(ByteBuffer.wrap(bytes))
                }
            }

            apoep?.let {
                return ARequest(protocol = APOEP, version, elements, it)
            } ?: throw IllegalStateException("APOEP packet not found")
        }
    }
}
