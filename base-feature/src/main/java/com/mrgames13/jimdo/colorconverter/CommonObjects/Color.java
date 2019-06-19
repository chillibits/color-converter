package com.mrgames13.jimdo.colorconverter.CommonObjects;

import androidx.annotation.NonNull;

public class Color implements Comparable {

    //Attribute
    private int id;
    private String name;
    private int color;
    private int red;
    private int green;
    private int blue;
    private long creation_timestamp = System.currentTimeMillis();

    public Color(int id, String name, int color, long creation_timestamp) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.red = android.graphics.Color.red(color);
        this.green = android.graphics.Color.green(color);
        this.blue = android.graphics.Color.blue(color);
        if(creation_timestamp != -1) this.creation_timestamp = creation_timestamp;
    }

    public Color(int id, String name, int red, int green, int blue, long creation_timestamp) {
        this.id = id;
        this.name = name;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.color = android.graphics.Color.argb(255, red, green, blue);
        if(creation_timestamp != -1) this.creation_timestamp = creation_timestamp;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public int getRed() {
        return red;
    }
    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
        return green;
    }
    public void setGreen(int green) {
        this.green = green;
    }

    public int getBlue() {
        return blue;
    }
    public void setBlue(int blue) {
        this.blue = blue;
    }

    public long getCreationTimestamp() {
        return creation_timestamp;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Color other = (Color) o;
        if(creation_timestamp < other.getCreationTimestamp()) {
            return 1;
        } else if(creation_timestamp > other.getCreationTimestamp()) {
            return -1;
        }
        return 0;
    }
}