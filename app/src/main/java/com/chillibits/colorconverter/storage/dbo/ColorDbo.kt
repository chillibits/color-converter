/*
 * Copyright © Marc Auberer 2017-2021. All rights reserved
 */

package com.chillibits.colorconverter.storage.dbo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "colors")
data class ColorDbo(
        @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int,
        @ColumnInfo(name = "name") var name: String,
        @ColumnInfo(name = "alpha") val alpha: Int,
        @ColumnInfo(name = "red") val red: Int,
        @ColumnInfo(name = "green") val green: Int,
        @ColumnInfo(name = "blue") val blue: Int,
        @ColumnInfo(name = "creation_timestamp") val creationTimestamp: Long
)
