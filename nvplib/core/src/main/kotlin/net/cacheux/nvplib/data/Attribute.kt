package net.cacheux.nvplib.data

import net.cacheux.bytonio.BinaryDeserializer
import net.cacheux.bytonio.annotations.DataObject
import net.cacheux.bytonio.annotations.Deserializer
import net.cacheux.bytonio.annotations.IgnoreEncoding
import net.cacheux.bytonio.utils.ByteArrayReader
import net.cacheux.bytonio.utils.readInt
import net.cacheux.bytonio.utils.readShort
import net.cacheux.bytonio.utils.reader

@DataObject
@Deserializer(AttributeDeserializer::class)
data class Attribute(
    val type: Int,
    val data: ByteArray,
    @IgnoreEncoding val value: Int = -1
) {
    companion object {
        const val ATTR_SYS_ID = 2436
        const val ATTR_ID_INSTNO = 2338
        const val ATTR_ID_MODEL = 2344
        const val ATTR_ID_PROD_SPECN = 2349
        const val ATTR_ID_TYPE = 2351
        const val ATTR_METRIC_STORE_CAPAC_CNT = 2369
        const val ATTR_METRIC_STORE_SAMPLE_ALG = 2371
        const val ATTR_METRIC_STORE_USAGE_CNT = 2372
        const val ATTR_NUM_SEG = 2385
        const val ATTR_OP_STAT = 2387
        const val ATTR_SEG_USAGE_CNT = 2427
        const val ATTR_TIME_REL = 2447
        const val ATTR_UNIT_CODE = 2454
        const val ATTR_DEV_CONFIG_ID = 2628
        const val ATTR_MDS_TIME_INFO = 2629
        const val ATTR_METRIC_SPEC_SMALL = 2630
        const val ATTR_REG_CERT_DATA_LIST = 2635
        const val ATTR_PM_STORE_CAPAB = 2637
        const val ATTR_PM_SEG_MAP = 2638
        const val ATTR_ATTRIBUTE_VAL_MAP = 2645
        const val ATTR_NU_VAL_OBS_SIMP = 2646
        const val ATTR_PM_STORE_LABEL_STRING = 2647
        const val ATTR_PM_SEG_LABEL_STRING = 2648
        const val ATTR_SYS_TYPE_SPEC_LIST = 2650
        const val ATTR_CLEAR_TIMEOUT = 2659
        const val ATTR_TRANSFER_TIMEOUT = 2660
        const val ATTR_ENUM_OBS_VAL_BASIC_BIT_STR = 2662
    }
}

object AttributeDeserializer: BinaryDeserializer<Attribute> {
    override fun fromByteArray(byteArray: ByteArray) =
        fromByteArrayReader(byteArray.reader())

    override fun fromByteArrayReader(reader: ByteArrayReader): Attribute {
        val type = reader.readShort()
        val len = reader.readShort()
        val data = reader.readByteArray(len)
        val value = when (data.size) {
            2 -> data.readShort()
            4 -> data.readInt()
            else -> -1
        }
        return Attribute(type, data, value)
    }
}
