/*
 * Copyright © 2019 Marc Auberer. All rights reserved.
 */

package com.mrgames13.jimdo.colorconverter.tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.mrgames13.jimdo.colorconverter.R

class ColorTools(var context: Context) {

    fun getVibrantColor(image: Bitmap): Int {
        val palette: Palette = Palette.from(image).generate()
        return palette.getVibrantColor(ContextCompat.getColor(context, R.color.gray))
    }

    fun getLightVibrantColor(image: Bitmap): Int {
        val palette: Palette = Palette.from(image).generate()
        return palette.getLightVibrantColor(ContextCompat.getColor(context, R.color.gray))
    }

    fun getDarkVibrantColor(image: Bitmap): Int {
        val palette: Palette = Palette.from(image).generate()
        return palette.getDarkVibrantColor(ContextCompat.getColor(context, R.color.gray))
    }

    fun getMutedColor(image: Bitmap): Int {
        val palette: Palette = Palette.from(image).generate()
        return palette.getMutedColor(ContextCompat.getColor(context, R.color.gray))
    }

    fun getLightMutedColor(image: Bitmap): Int {
        val palette: Palette = Palette.from(image).generate()
        return palette.getLightMutedColor(ContextCompat.getColor(context, R.color.gray))
    }

    fun getDarkMutedColor(image: Bitmap): Int {
        val palette: Palette = Palette.from(image).generate()
        return palette.getDarkMutedColor(ContextCompat.getColor(context, R.color.gray))
    }

    fun darkenColor(color: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] *= 0.8f
        return Color.HSVToColor(hsv)
    }

    fun getTextColor(color: Int): Int {
        val sum = Color.red(color) + Color.green(color) + Color.blue(color)
        return if (sum > 384) Color.BLACK else Color.WHITE
    }
}