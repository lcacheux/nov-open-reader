package net.cacheux.nvplib

import java.nio.ByteBuffer

data class TransceiveResult(
    val content: ByteBuffer,
    val success: Boolean
)
