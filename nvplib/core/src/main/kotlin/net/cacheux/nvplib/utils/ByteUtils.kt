package net.cacheux.nvplib.utils

import net.cacheux.nvplib.data.Encodable
import java.nio.ByteBuffer
import kotlin.reflect.KProperty
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf

fun ByteBuffer.putUnsignedByte(s: Int) {
    put((s and 0xff).toByte())
}

fun ByteBuffer.putUnsignedShort(s: Int) {
    putShort((s and 0xffff).toShort())
}

fun ByteBuffer.putUnsignedInt(s: Int) {
    putInt(s and 0xffffffff.toInt())
}

fun ByteBuffer.getUnsignedInt(): Int {
    return (this.getInt().toLong() and 0xffffffffL).toInt()
}

fun ByteBuffer.getUnsignedShort(): Int {
    return getShort().toInt() and 0x0000ffff
}

fun ByteBuffer.getUnsignedByte(): Int {
    return get().toInt() and 0x000000ff
}

fun ByteBuffer.getByteArray(): ByteArray {
    val size = getUnsignedShort()
    val buffer = ByteArray(size)
    get(buffer, 0, size)
    return buffer
}

fun ByteBuffer.getIndexedString() =
    String(getByteArray()).replace(
        "\u0000",
        ""
    )

fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
fun ByteArray.wrap(): ByteBuffer = ByteBuffer.wrap(this)

fun <T> encodedSize(data: T): Int {
    val clazz = data!!::class

    return clazz.members.mapNotNull {
        if (it is KProperty<*>) {
            if (it.returnType.isSubtypeOf(Encodable::class.createType())) {
                encodedSize(data)
            } else when (it.returnType.classifier) {
                ByteArray::class -> (it.getter.call(data) as ByteArray).size
                Int::class -> 4
                Short::class -> 2
                Byte::class -> 1
                else -> 0
            }
        } else null
    }.sum()
}

fun <T> encodeToByteArray(data: T): ByteArray {
    val clazz = data!!::class

    if (!clazz.isData) {
        throw IllegalArgumentException("Parameter must be a data class")
    }

    val buffer = ByteBuffer.allocate(encodedSize(data))
    // Use parameters from constructor to keep order
    clazz.constructors.first().parameters.forEach { param ->
        val prop = clazz.members.find { it.name == param.name }
        if (prop is KProperty<*>) {
            when (prop.returnType.classifier) {
                ByteArray::class -> buffer.put(prop.getter.call(data) as ByteArray)
                Int::class -> buffer.putInt(prop.getter.call(data) as Int)
                Short::class -> buffer.putShort(prop.getter.call(data) as Short)
                Byte::class -> buffer.put(prop.getter.call(data) as Byte)
            }
        }
    }

    return buffer.array()
}
