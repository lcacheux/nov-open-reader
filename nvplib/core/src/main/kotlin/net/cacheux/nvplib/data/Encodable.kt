package net.cacheux.nvplib.data

import net.cacheux.nvplib.annotations.IsByte
import net.cacheux.nvplib.annotations.IsShort
import java.nio.ByteBuffer
import kotlin.reflect.KProperty
import kotlin.reflect.full.createType
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubtypeOf

open class Encodable {

    open fun encodedSize(): Int {
        return this::class.members.mapNotNull {
            if (it is KProperty<*>) {
                if (it.returnType.isSubtypeOf(Encodable::class.createType())) {
                    (it.getter.call(this) as Encodable).encodedSize() + 2
                } else if (it.returnType.isSubtypeOf(Encodable::class.createType(nullable = true))) {
                    (it.getter.call(this) as Encodable?)?.let { it.encodedSize() + 2 } ?: 0
                } else if (it.returnType.classifier == ByteArray::class) {
                    (it.getter.call(this) as ByteArray).size + 2
                } else if (it.returnType.classifier == Int::class) {
                    when {
                        it.hasAnnotation<IsShort>() -> 2
                        it.hasAnnotation<IsByte>() -> 1
                        else -> 4
                    }
                } else 0
            } else null
        }.sum()
    }

    open fun toByteArray(): ByteArray {
        val clazz = this::class

        val size = encodedSize()
        val buffer = ByteBuffer.allocate(size)

        // Use parameters from constructor to keep order
        clazz.constructors.first().parameters.forEach { param ->
            val prop = clazz.members.find { it.name == param.name }

            if (prop is KProperty<*>) {
                if (prop.returnType.isSubtypeOf(Encodable::class.createType())) {
                    val array = (prop.getter.call(this) as Encodable).toByteArray()
                    buffer.putShort(array.size.toShort())
                    buffer.put(array)
                } else if (prop.returnType.isSubtypeOf(Encodable::class.createType(nullable = true))) {
                    (prop.getter.call(this) as Encodable?)?.let {
                        val array = it.toByteArray()
                        buffer.putShort(array.size.toShort())
                        buffer.put(array)
                    }
                } else if (prop.returnType.classifier == ByteArray::class) {
                    val array = prop.getter.call(this) as ByteArray
                    buffer.putShort(array.size.toShort())
                    buffer.put(array)
                } else if (prop.returnType.classifier == Int::class) {
                    when {
                        prop.hasAnnotation<IsShort>() -> buffer.putShort((prop.getter.call(this) as Int).toShort())
                        prop.hasAnnotation<IsByte>() -> buffer.put((prop.getter.call(this) as Int).toByte())
                        else -> buffer.putInt(prop.getter.call(this) as Int)
                    }
                }
            }
        }

        return buffer.array()
    }
}