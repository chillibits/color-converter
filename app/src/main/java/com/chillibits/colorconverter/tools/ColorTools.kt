/*
 * Copyright © Marc Auberer 2017-2023. All rights reserved
 */

package com.chillibits.colorconverter.tools

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.chillibits.colorconverter.shared.Constants
import com.mrgames13.jimdo.colorconverter.R
import java.util.*
import kotlin.math.max
import kotlin.math.roundToInt

class ColorTools(private var context: Context) {
    fun getVibrantColor(image: Bitmap) = Palette.from(image).generate()
        .getVibrantColor(ContextCompat.getColor(context, R.color.gray))

    fun getLightVibrantColor(image: Bitmap) = Palette.from(image).generate()
        .getLightVibrantColor(ContextCompat.getColor(context, R.color.gray))

    fun getDarkVibrantColor(image: Bitmap) = Palette.from(image).generate()
        .getDarkVibrantColor(ContextCompat.getColor(context, R.color.gray))

    fun getMutedColor(image: Bitmap) =
        Palette.from(image).generate().getMutedColor(ContextCompat.getColor(context, R.color.gray))

    fun getLightMutedColor(image: Bitmap) = Palette.from(image).generate()
        .getLightMutedColor(ContextCompat.getColor(context, R.color.gray))

    fun getDarkMutedColor(image: Bitmap) = Palette.from(image).generate()
        .getDarkMutedColor(ContextCompat.getColor(context, R.color.gray))

    fun getTextColor(activity: Activity, color: Int) = if (Color.alpha(color) > 127) {
        val sum = Color.red(color) + Color.green(color) + Color.blue(color)
        if (sum > 384) Color.BLACK else Color.WHITE
    } else {
        when (activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> Color.BLACK
            Configuration.UI_MODE_NIGHT_YES -> Color.WHITE
            else -> Color.BLACK
        }
    }

    fun getCmykFromRgb(red: Int, green: Int, blue: Int): Array<Int> {
        if (red == 0 && green == 0 && blue == 0) return arrayOf(0, 0, 0, 100)
        val redPercentage = red / 255.0 * 100.0
        val greenPercentage = green / 255.0 * 100.0
        val bluePercentage = blue / 255.0 * 100.0
        val k = 100.0 - max(max(redPercentage, greenPercentage), bluePercentage)
        val c = (100.0 - redPercentage - k) / (100.0 - k) * 100.0
        val m = (100.0 - greenPercentage - k) / (100.0 - k) * 100.0
        val y = (100.0 - bluePercentage - k) / (100.0 - k) * 100.0
        return arrayOf(c.roundToInt(), m.roundToInt(), y.roundToInt(), k.roundToInt())
    }

    fun getRandomColor(): com.chillibits.colorconverter.model.Color {
        val random = Random(System.currentTimeMillis())
        val red = random.nextInt(256)
        val green = random.nextInt(256)
        val blue = random.nextInt(256)
        return com.chillibits.colorconverter.model.Color(
            0,
            Constants.NAME_SELECTED_COLOR,
            255,
            red,
            green,
            blue,
            -1
        )
    }
}