package com.ingenico.android.common.nfc.smartcardio

import java.nio.ByteBuffer
import javax.smartcardio.CardChannel

class Channel private constructor(
    private val cardChannel: CardChannel
) {
    companion object {
        fun create(cardChannel: CardChannel) = Channel(cardChannel)
    }

    fun transmit(input: ByteBuffer, output: ByteBuffer): Int {
        return cardChannel.transmit(input, output)
    }

    fun close() {
        //cardChannel.close()
    }
}
