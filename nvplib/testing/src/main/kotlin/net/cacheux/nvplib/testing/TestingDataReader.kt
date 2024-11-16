package net.cacheux.nvplib.testing

import net.cacheux.nvplib.DataReader
import net.cacheux.nvplib.utils.ByteArrayStore
import java.io.InputStream

/**
 * For testing purpose, implementation of a [DataReader] that will read from an InputStream, ie.
 * an external file. The input data is not used in this case.
 */
class TestingDataReader(
    inputStream: InputStream
): DataReader {

    private val dataStore = ByteArrayStore.fromInputStream(inputStream)
    private val iterator = dataStore.content.iterator()

    override fun readData(input: ByteArray) = iterator.next()
}
