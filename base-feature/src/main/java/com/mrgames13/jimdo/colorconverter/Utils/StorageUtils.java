package com.mrgames13.jimdo.colorconverter.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mrgames13.jimdo.colorconverter.CommonObjects.Color;

import java.util.ArrayList;
import java.util.Collections;

public class StorageUtils extends SQLiteOpenHelper{

    public static final String TABLE_COLORS = "Colors";

    //Variablen

    public StorageUtils(Context context) {
        super(context, "database.db", null, 1);
    }

    // --------------------------------------- Datenbank -------------------------------------------

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            //Tabellen erstellen
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_COLORS + " (id integer, name text, red integer, green integer, blue integer, creation_timestamp integer);");
        } catch (Exception e) {
            Log.e("ColorConverter", "Database creation error: ", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {}

    private void addRecord(String table, ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(table, null, values);
    }

    public void removeRecord(String table, int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(table, "id=?", new String[] {String.valueOf(id)});
    }

    public void execSQL(String command) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(command);
    }

    // ------------------------------------ Color Management ---------------------------------------

    public void saveColor(Color color) {
        try{
            ContentValues values = new ContentValues();
            values.put("id", loadColors().size());
            values.put("name", color.getName());
            values.put("red", color.getRed());
            values.put("green", color.getGreen());
            values.put("blue", color.getBlue());
            values.put("creation_timestamp", color.getCreationTimestamp());
            addRecord(TABLE_COLORS, values);
        } catch (Exception e) {
            Log.e("ColorConverter", "Error storing color", e);
        }
    }

    public ArrayList<Color> loadColors() {
        try{
            SQLiteDatabase db = getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_COLORS, null);
            ArrayList<Color> colors = new ArrayList<>();
            while(cursor.moveToNext()) {
                colors.add(new Color(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5)));
            }
            cursor.close();
            Collections.sort(colors);
            return colors;
        } catch (Exception e) {
            Log.e("ChatLet", "Error loading colors", e);
        }
        return new ArrayList<>();
    }
}