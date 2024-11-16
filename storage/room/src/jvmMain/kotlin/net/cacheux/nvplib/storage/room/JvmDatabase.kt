package net.cacheux.nvplib.storage.room

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

fun databaseBuilder(path: String) = Room.databaseBuilder<NvpDatabase>(name = path)
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()
