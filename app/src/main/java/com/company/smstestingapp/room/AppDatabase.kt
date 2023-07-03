package com.company.smstestingapp.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@Database(entities = [SmsSaved::class], version = 1,exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): SmsDao?

    val databaseWriteExecutor: ExecutorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

    companion object{

        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val NUMBER_OF_THREADS = 4

        @JvmStatic
        fun getDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java, "sms_database"
                        )
                            .build()
                    }
                }
            }
            return INSTANCE
        }
    }

}