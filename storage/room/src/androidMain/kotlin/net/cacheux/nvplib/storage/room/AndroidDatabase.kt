package net.cacheux.nvplib.storage.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun databaseBuilder(context: Context): RoomDatabase.Builder<NvpDatabase> {
    val dbFile = context.applicationContext.getDatabasePath("nov_open_reader.db")
    return Room.databaseBuilder<NvpDatabase>(
        context = context.applicationContext,
        name = dbFile.absolutePath
    )
}
