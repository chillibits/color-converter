/*
 * Copyright © Marc Auberer 2017-2021. All rights reserved
 */

package com.chillibits.colorconverter.storage.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.chillibits.colorconverter.storage.dbo.ColorDbo

@Dao
interface ColorDao {
    @Query("SELECT * FROM `Colors`")
    fun getAll(): LiveData<List<ColorDbo>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(color: ColorDbo)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(colors: List<ColorDbo>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(color: ColorDbo)

    @Delete
    fun delete(color: ColorDbo)
}