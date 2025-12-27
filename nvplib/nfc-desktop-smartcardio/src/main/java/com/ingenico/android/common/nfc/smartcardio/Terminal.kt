package com.ingenico.android.common.nfc.smartcardio

import javax.smartcardio.CardTerminal
import javax.smartcardio.TerminalFactory

class Terminal private constructor(
    private val cardTerminal: CardTerminal
) {
    companion object {
        fun getFirstTerminal(): Terminal {
            val terminals = TerminalFactory.getDefault().terminals().list()
            check(terminals.isNotEmpty()) { "No NFC reader detected" }

            return Terminal(terminals.first())
        }
    }

    val name: String
        get() = cardTerminal.name

    fun waitForCard() {
        cardTerminal.waitForCardPresent(0)
    }

    fun waitForEnd() {
        cardTerminal.waitForCardAbsent(0)
    }

    fun connect(): Channel {

        val card = cardTerminal.connect("*")
        println("Card protocol: ${card.protocol}")

        return Channel.create(card.basicChannel)
    }
}
