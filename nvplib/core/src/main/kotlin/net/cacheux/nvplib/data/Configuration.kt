package net.cacheux.nvplib.data

import net.cacheux.bytonio.utils.ByteArrayReader

data class Configuration(
    val id: Int,
    val handle: Int,
    val nbSegment: Int,
    val totalEntries: Int,
    val unitCode: Int,
    val totalStorage: Int,
    val attributes: List<Attribute> = listOf()
) {
    companion object {
        fun fromByteArrayReader(reader: ByteArrayReader): Configuration {
            val id = reader.readShort()
            val count = reader.readShort()
            reader.readShort() // len

            var nbSegment = -1
            var totalEntries = -1
            var unitCode = -1
            var totalStorage = -1

            val attributes = mutableListOf<Attribute>()

            repeat(count) {
                reader.readShort() // cls
                reader.readShort() // handle
                val attrCount = reader.readShort()
                reader.readShort() //attrLen

                repeat(attrCount) {
                    val attribute = AttributeDeserializer.fromByteArrayReader(reader)

                    when (attribute.type) {
                        Attribute.ATTR_NUM_SEG -> nbSegment = attribute.value
                        Attribute.ATTR_METRIC_STORE_USAGE_CNT -> totalEntries = attribute.value
                        Attribute.ATTR_UNIT_CODE -> unitCode = attribute.value
                        Attribute.ATTR_METRIC_STORE_CAPAC_CNT -> totalStorage = attribute.value
                        Attribute.ATTR_ATTRIBUTE_VAL_MAP -> {

                        }
                    }
                    attributes.add(attribute)
                }
            }

            return Configuration(id, 0, nbSegment, totalEntries, unitCode, totalStorage, attributes)
        }
    }
}
