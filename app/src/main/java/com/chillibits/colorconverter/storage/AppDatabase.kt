/*
 * Copyright © Marc Auberer 2017-2021. All rights reserved
 */

package com.chillibits.colorconverter.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chillibits.colorconverter.storage.dao.ColorDao
import com.chillibits.colorconverter.storage.dbo.ColorDbo

// Increase version whenever the structure of the local db changes
@Database(entities = [ColorDbo::class], exportSchema = false, version = 3)
abstract class AppDatabase: RoomDatabase() {
    abstract fun colorDao(): ColorDao
}