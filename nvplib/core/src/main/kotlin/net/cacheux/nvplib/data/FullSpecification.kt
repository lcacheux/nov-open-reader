package net.cacheux.nvplib.data

import net.cacheux.bytonio.annotations.IgnoreEncoding
import net.cacheux.bytonio.utils.reader
import net.cacheux.nvplib.data.Attribute.Companion.ATTR_ID_MODEL
import net.cacheux.nvplib.data.Attribute.Companion.ATTR_ID_PROD_SPECN
import net.cacheux.nvplib.data.Attribute.Companion.ATTR_TIME_REL
import net.cacheux.nvplib.utils.getIndexedString

data class FullSpecification(
    val specification: Specification,
    val relativeTime: Long = 0L,
    @IgnoreEncoding val model: List<String> = listOf()
): DummyBinarySerializable() {
    companion object {
        fun fromAttributes(attributes: List<Attribute>): FullSpecification {
            var specification = Specification()
            var relativeTime = 0L
            val model = mutableListOf<String>()
            attributes.forEach {
                when (it.type) {
                    ATTR_ID_PROD_SPECN -> {
                        specification = Specification.fromByteArrayReader(it.data.reader())
                    }
                    ATTR_TIME_REL -> {
                        relativeTime = it.data.reader().readInt().toLong()
                    }
                    ATTR_ID_MODEL -> {
                        val reader = it.data.reader()
                        while (reader.hasRemaining()) {
                            model.add(reader.getIndexedString())
                        }
                    }
                }
            }
            return FullSpecification(specification, relativeTime, model)
        }
    }
}
