/*
 * Copyright © Marc Auberer 2021. All rights reserved
 */

package com.chillibits.colorconverter.model

import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import org.jetbrains.annotations.NotNull

class Color(
    var id: Int,
    var name: String,
    var color: Int,
    var creationTimestamp: Long = System.currentTimeMillis()
): Comparable<Color> {

    var alpha = color.alpha
    var red = color.red
    var green = color.green
    var blue = color.blue

    constructor(id: Int, name: String, alpha: Int, red: Int, green: Int, blue: Int, creationTimestamp: Long):
            this(id, name, android.graphics.Color.argb(alpha, red, green, blue), creationTimestamp)

    override fun compareTo(@NotNull other: Color) = other.creationTimestamp.compareTo(creationTimestamp)
}