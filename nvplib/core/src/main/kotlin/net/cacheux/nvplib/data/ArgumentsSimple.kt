package net.cacheux.nvplib.data

import net.cacheux.nvplib.annotations.IsShort

data class ArgumentsSimple(
    @IsShort val handle: Int,
    @IsShort val size: Int = 0,
    @IsShort val size2: Int = 0,
): Encodable()
