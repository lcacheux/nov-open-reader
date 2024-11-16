package net.cacheux.nvplib.data

import net.cacheux.nvplib.annotations.IsInt
import net.cacheux.nvplib.annotations.IsShort

data class EventRequest(
    @IsShort val handle: Int,
    @IsInt val currentTime: Int,
    @IsShort val type: Int,
    val data: ByteArray
): Encodable()
