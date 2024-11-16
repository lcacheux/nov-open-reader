package net.cacheux.nvplib.data

import net.cacheux.nvplib.data.Attribute.Companion.ATTR_ID_MODEL
import net.cacheux.nvplib.data.Attribute.Companion.ATTR_ID_PROD_SPECN
import net.cacheux.nvplib.data.Attribute.Companion.ATTR_TIME_REL
import net.cacheux.nvplib.utils.getIndexedString
import net.cacheux.nvplib.utils.getUnsignedInt
import net.cacheux.nvplib.utils.wrap

data class FullSpecification(
    val specification: Specification,
    val relativeTime: Long = 0L,
    val model: List<String> = listOf()
): Encodable() {
    companion object {
        fun fromAttributes(attributes: List<Attribute>): FullSpecification {
            var specification = Specification()
            var relativeTime = 0L
            val model = mutableListOf<String>()
            attributes.forEach {
                when (it.type) {
                    ATTR_ID_PROD_SPECN -> {
                        specification = Specification.fromByteBuffer(it.data.wrap())
                    }
                    ATTR_TIME_REL -> {
                        relativeTime = it.data.wrap().getUnsignedInt().toLong()
                    }
                    ATTR_ID_MODEL -> {
                        val buffer = it.data.wrap()
                        while (buffer.hasRemaining()) {
                            model.add(buffer.getIndexedString())
                        }
                    }
                }
            }
            return FullSpecification(specification, relativeTime, model)
        }
    }
}
