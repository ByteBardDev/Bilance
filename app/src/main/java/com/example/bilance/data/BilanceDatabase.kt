package com.example.bilance.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import java.util.Date

@Database(
    entities = [User::class, Transaction::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BilanceDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: BilanceDatabase? = null

        fun getDatabase(context: Context): BilanceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BilanceDatabase::class.java,
                    "bilance_database"
                ).fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 