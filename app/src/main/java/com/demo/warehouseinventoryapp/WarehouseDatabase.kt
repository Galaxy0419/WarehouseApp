package com.demo.warehouseinventoryapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(entities = [WarehouseItem::class], version = 1, exportSchema = false)
abstract class WarehouseDatabase : RoomDatabase() {
    abstract fun itemDao(): WarehouseDao

    companion object {
        private const val THREAD_NUMBER = 4
        private const val DATABASE_NAME = "warehouse_database"

        val dataWriteExecutor: ExecutorService = Executors.newFixedThreadPool(THREAD_NUMBER)

        @Volatile
        private var INSTANCE: WarehouseDatabase? = null
        fun getDatabase(context: Context): WarehouseDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(context.applicationContext,
                WarehouseDatabase::class.java, DATABASE_NAME).build()
        }
    }
}