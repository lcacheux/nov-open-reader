package net.cacheux.nvp.model

data class IoB(
    val time: Long,
    val remaining: InsulinUnit,
    val current: InsulinUnit,
    val delta: Long,
    val serial: String = ""
)