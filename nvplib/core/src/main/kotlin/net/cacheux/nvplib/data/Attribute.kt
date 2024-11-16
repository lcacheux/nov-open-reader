package net.cacheux.nvplib.data

import net.cacheux.nvplib.annotations.IsShort
import net.cacheux.nvplib.utils.getByteArray
import net.cacheux.nvplib.utils.getUnsignedInt
import net.cacheux.nvplib.utils.getUnsignedShort
import net.cacheux.nvplib.utils.wrap
import java.nio.ByteBuffer

data class Attribute(
    @IsShort val type: Int,
    val data: ByteArray,
    val value: Int
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

        fun fromByteBuffer(buffer: ByteBuffer) : Attribute {
            val type = buffer.getUnsignedShort()
            val data = buffer.getByteArray()
            val value = when (data.size) {
                2 -> data.wrap().getUnsignedShort()
                4 -> data.wrap().getUnsignedInt()
                else -> -1
            }
            return Attribute(type, data, value)
        }
    }
}
