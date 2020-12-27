/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.colorconverter.storage.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chillibits.colorconverter.storage.dbo.ColorDbo

@Dao
interface ColorDao {
    @Query("SELECT * FROM `Colors`")
    fun getAll(): LiveData<List<ColorDbo>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(record: ColorDbo)
}