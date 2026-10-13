package com.learn.ourmarketplace.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [ProductEntity::class], version = 2, exportSchema = true)
abstract class MarketplaceDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE products ADD COLUMN isUserCreated INTEGER NOT NULL DEFAULT 0"
                )
                db.execSQL(
                    "ALTER TABLE products ADD COLUMN isSynced INTEGER NOT NULL DEFAULT 1"
                )
            }
        }
    }
}
