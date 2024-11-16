package net.cacheux.nvplib.nfc

import android.nfc.tech.IsoDep
import net.cacheux.nvplib.DataReader

open class NfcDataReader(
    private val isoDep: IsoDep
): DataReader {
    override fun readData(input: ByteArray): ByteArray = isoDep.transceive(input)
}
