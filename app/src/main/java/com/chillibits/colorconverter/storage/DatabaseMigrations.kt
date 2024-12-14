/*
 * Copyright © Marc Auberer 2017-2024. All rights reserved
 */

package com.chillibits.colorconverter.storage

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Migration from SQLite API to Room
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Colors RENAME TO tmp;")
        db.execSQL("CREATE TABLE colors (id INTEGER PRIMARY KEY NOT NULL, name TEXT NOT NULL, red INTEGER NOT NULL, green INTEGER NOT NULL, blue INTEGER NOT NULL, creation_timestamp INTEGER NOT NULL, alpha INTEGER NOT NULL);")
        db.execSQL("INSERT INTO colors SELECT * FROM tmp;")
    }
}