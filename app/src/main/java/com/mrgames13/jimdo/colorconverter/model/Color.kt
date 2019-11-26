package com.mrgames13.jimdo.colorconverter.model

import org.jetbrains.annotations.NotNull

class Color: Comparable<Color> {

    // Attributes
    var id: Int
    var name: String
    var color: Int
    var red: Int
    var green: Int
    var blue: Int
    var creationTimestamp: Long = System.currentTimeMillis()

    constructor(id: Int, name: String, color: Int, creationTimestamp: Long) {
        this.id = id
        this.name = name
        this.color = color
        this.red = android.graphics.Color.red(color)
        this.green = android.graphics.Color.green(color)
        this.blue = android.graphics.Color.blue(color)
        if(creationTimestamp != -1L) this.creationTimestamp = creationTimestamp
    }

    constructor(id: Int, name: String, red: Int, green: Int, blue: Int, creationTimestamp: Long) {
        this.id = id;
        this.name = name
        this.color = android.graphics.Color.argb(255, red, green, blue)
        this.red = red
        this.green = green
        this.blue = blue
        if(creationTimestamp != -1L) this.creationTimestamp = creationTimestamp
    }

    override fun compareTo(@NotNull other: Color): Int {
        return other.creationTimestamp.compareTo(creationTimestamp)
    }
}