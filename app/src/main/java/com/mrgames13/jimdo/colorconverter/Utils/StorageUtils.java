package com.mrgames13.jimdo.colorconverter.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mrgames13.jimdo.colorconverter.CommonObjects.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class StorageUtils extends SQLiteOpenHelper{

    //Konstanten
    private final String DEFAULT_STRING_VALUE = "";
    private final int DEFAULT_INT_VALUE = 0;
    private final boolean DEFAULT_BOOLEAN_VALUE = false;
    public static final String TABLE_COLORS = "Colors";

    //Variablen als Objekte
    private Context context;
    private SharedPreferences prefs;
    private SharedPreferences.Editor e;
    private Resources res;

    //Variablen

    public StorageUtils(Context context, Resources res) {
        super(context, "database.db", null, 1);
        this.context = context;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.res = res;
    }

    public void putString(String name, String value) {
        e = prefs.edit();
        e.putString(name, value);
        e.commit();
    }

    public void putInt(String name, int value) {
        e = prefs.edit();
        e.putInt(name, value);
        e.commit();
    }

    public void putBoolean(String name, boolean value) {
        e = prefs.edit();
        e.putBoolean(name, value);
        e.commit();
    }

    public void putStringSet(String name, Set<String> value) {
        e = prefs.edit();
        e.putStringSet(name, value);
        e.commit();
    }

    public String getString(String name) {
        return prefs.getString(name, DEFAULT_STRING_VALUE);
    }

    public int getInt(String name) {
        return prefs.getInt(name, DEFAULT_INT_VALUE);
    }

    public boolean getBoolean(String name) {
        return prefs.getBoolean(name, DEFAULT_BOOLEAN_VALUE);
    }

    public Set<String> getStringSet(String name) {
        return prefs.getStringSet(name, null);
    }

    public String getString(String name, String default_value) {
        return prefs.getString(name, default_value);
    }

    public int getInt(String name, int default_value) {
        return prefs.getInt(name, default_value);
    }

    public boolean getBoolean(String name, boolean default_value) {
        return prefs.getBoolean(name, default_value);
    }

    public void removePair(String name) {
        e = prefs.edit();
        e.remove(name);
        e.commit();
    }

    public void clearData(String name) {
        prefs.edit().remove(name).commit();
    }

    public void clear() {
        prefs.edit().clear().commit();
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
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {

        }
    }

    public long addRecord(String table, ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(table, null, values);
        return id;
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