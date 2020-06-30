/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.colorconverter.model

import org.jetbrains.annotations.NotNull

class Color: Comparable<Color> {

    // Attributes
    val id: Int
    var name: String
    var color: Int
    var alpha: Int
    var red: Int
    var green: Int
    var blue: Int
    var creationTimestamp = System.currentTimeMillis()

    constructor(id: Int, name: String, color: Int, creationTimestamp: Long) {
        this.id = id
        this.name = name
        this.color = color
        this.alpha = android.graphics.Color.alpha(color)
        this.red = android.graphics.Color.red(color)
        this.green = android.graphics.Color.green(color)
        this.blue = android.graphics.Color.blue(color)
        if(creationTimestamp != -1L) this.creationTimestamp = creationTimestamp
    }

    constructor(id: Int, name: String, alpha: Int, red: Int, green: Int, blue: Int, creationTimestamp: Long):
            this(id, name, android.graphics.Color.argb(alpha, red, green, blue), creationTimestamp)

    override fun compareTo(@NotNull other: Color) = other.creationTimestamp.compareTo(creationTimestamp)
}