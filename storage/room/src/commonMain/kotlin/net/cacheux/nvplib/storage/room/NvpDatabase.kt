package net.cacheux.nvplib.storage.room

import androidx.room.Database
import androidx.room.RoomDatabase
import net.cacheux.nvplib.storage.room.dao.DoseDao
import net.cacheux.nvplib.storage.room.entities.RoomDose
import net.cacheux.nvplib.storage.room.entities.RoomPen

@Database(entities = [RoomDose::class, RoomPen::class], version = 1, exportSchema = false)
abstract class NvpDatabase: RoomDatabase() {
    abstract fun doseDao(): DoseDao
}
