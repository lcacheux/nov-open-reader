package net.cacheux.nvplib

import net.cacheux.bytonio.utils.ByteArrayReader

data class TransceiveResult(
    val content: ByteArrayReader,
    val success: Boolean
)
