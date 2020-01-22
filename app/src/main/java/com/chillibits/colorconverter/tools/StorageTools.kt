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

class StorageTools(context: Context): SQLiteOpenHelper(context, "database.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        try {
            // Create tables
            db?.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_COLORS (id integer PRIMARY KEY, name text, red integer, green integer, blue integer, creation_timestamp integer);")
        } catch (e: Exception) {
            Log.e("ColorConverter", "Database creation error: ", e)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    private fun addRecord(table: String, values: ContentValues) {
        writableDatabase.insert(table, null, values)
    }

    private fun removeRecord(table: String?, id: Int) {
        writableDatabase.delete(table, "id=?", arrayOf(id.toString()))
    }

    private fun execSQL(command: String?) {
        writableDatabase.execSQL(command)
    }

    // ------------------------------------ Color Management ---------------------------------------

    fun addColor(color: Color) {
        try {
            val values = ContentValues()
            values.put("id", loadColors().size)
            values.put("name", color.name)
            values.put("red", color.red)
            values.put("green", color.green)
            values.put("blue", color.blue)
            values.put("creation_timestamp", color.creationTimestamp)
            addRecord(TABLE_COLORS, values)
        } catch (e: java.lang.Exception) {
            Log.e("ColorConverter", "Error storing color", e)
        }
    }

    fun updateColor(id: Int, newName: String) {
        execSQL("UPDATE $TABLE_COLORS SET name='$newName' WHERE id=$id")
    }

    fun removeColor(id: Int) {
        removeRecord(TABLE_COLORS, id)
    }

    fun loadColors(): ArrayList<Color> {
        try {
            val db = writableDatabase
            val cursor = db.rawQuery(
                "SELECT * FROM $TABLE_COLORS",
                null
            )
            val colors: ArrayList<Color> = ArrayList()
            while (cursor.moveToNext()) {
                colors.add(
                    Color(
                        cursor.getInt(0),
                        cursor.getString(1),
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
        } catch (e: java.lang.Exception) {
            Log.e("ChatLet", "Error loading colors", e)
        }
        return ArrayList()
    }
}