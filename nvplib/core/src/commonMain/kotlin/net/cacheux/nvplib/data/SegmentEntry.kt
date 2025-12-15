package net.cacheux.nvplib.data

import net.cacheux.bytonio.annotations.DataObject
import net.cacheux.bytonio.annotations.EncodeAsShort

@DataObject
data class SegmentEntry(
    @EncodeAsShort val classId: Int,
    @EncodeAsShort val metricType: Int,
    @EncodeAsShort val otype: Int,
    @EncodeAsShort val handle: Int,
    @EncodeAsShort val amCount: Int,
    val data: ByteArray
)
