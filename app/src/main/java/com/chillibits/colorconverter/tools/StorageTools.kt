/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.colorconverter.tools

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.chillibits.colorconverter.model.Color
import java.util.*

// Constants
const val TABLE_COLORS: String = "Colors"

class StorageTools(val context: Context): SQLiteOpenHelper(context, "database.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase?) {
        // Create tables
        db?.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_COLORS (id integer PRIMARY KEY, name text, red integer, green integer, blue integer, creation_timestamp integer, alpha integer);")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if(oldVersion == 1 && newVersion == 2) {
            db?.execSQL("ALTER TABLE $TABLE_COLORS ADD COLUMN alpha integer DEFAULT 255")
        }
    }

    // ------------------------------------ Shared Preference --------------------------------------

    fun putBoolean(name: String, value: Boolean)
            = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        .edit().putBoolean(name, value).apply()

    fun getBoolean(name: String, default: Boolean = false)
            = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        .getBoolean(name, default)

    // ------------------------------------ Color Management ---------------------------------------

    fun addColor(color: Color) {
        try {
            val values = ContentValues().apply {
                put("id", color.color)
                put("name", color.name)
                put("alpha", color.alpha)
                put("red", color.red)
                put("green", color.green)
                put("blue", color.blue)
                put("creation_timestamp", color.creationTimestamp)
            }
            writableDatabase.insert(TABLE_COLORS, null, values)
        } catch (e: Exception) {
            Log.e("ColorConverter", "Error saving color", e)
        }
    }

    fun updateColor(id: Int, newName: String) =
        writableDatabase.execSQL("UPDATE $TABLE_COLORS SET name='$newName' WHERE id=$id")

    fun removeColor(id: Int) =
        writableDatabase.delete(TABLE_COLORS, "id=?", arrayOf(id.toString()))

    fun loadColors(): List<Color> {
        try {
            val cursor = writableDatabase.rawQuery(
                "SELECT * FROM $TABLE_COLORS",
                null
            )
            val colors: ArrayList<Color> = ArrayList()
            while (cursor.moveToNext()) {
                colors.add(
                    Color(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(6),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getInt(4),
                        cursor.getLong(5)
                    )
                )
            }
            cursor.close()
            colors.sort()
            return colors
        } catch (e: Exception) {
            Log.e("ChatLet", "Error loading colors", e)
        }
        return emptyList()
    }
}