package net.cacheux.nvp.model

@JvmInline
value class InsulinUnit(private val v: Int) {
    constructor(f: Float) : this((f * 10).toInt())
    constructor(f: Double) : this((f * 10).toInt())

    fun toFloat() = v.toFloat() / 10
    fun toInt() = v

    operator fun minus(other: InsulinUnit): InsulinUnit {
        return InsulinUnit(this.v - other.v)
    }
}


