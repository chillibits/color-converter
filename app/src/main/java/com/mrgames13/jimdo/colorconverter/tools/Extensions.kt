package com.mrgames13.jimdo.colorconverter.tools

import android.content.Context
import android.content.res.ColorStateList
import android.widget.ImageView
import androidx.core.widget.ImageViewCompat
import com.mrgames13.jimdo.colorconverter.model.Color

fun Context.rgb(color: Color): Int {
    return android.graphics.Color.rgb(color.red, color.green, color.blue)
}

fun ImageView.setTint(color: Int) {
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
}