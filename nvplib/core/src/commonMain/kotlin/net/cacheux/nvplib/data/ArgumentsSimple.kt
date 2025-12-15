package net.cacheux.nvplib.data

import net.cacheux.bytonio.BinarySerializable
import net.cacheux.bytonio.annotations.DataObject
import net.cacheux.bytonio.annotations.EncodeAsShort
import net.cacheux.nvplib.generated.ArgumentsSimpleSerializer

@DataObject
data class ArgumentsSimple(
    @EncodeAsShort val handle: Int,
    @EncodeAsShort val size: Int = 0,
    @EncodeAsShort val size2: Int = 0,
): BinarySerializable {
    override fun getBinarySize() = ArgumentsSimpleSerializer.getBinarySize(this)
    override fun toByteArray() = ArgumentsSimpleSerializer.toByteArray(this)
}
