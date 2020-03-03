/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.colorconverter.tools

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.widget.ImageView
import androidx.core.widget.ImageViewCompat

fun ImageView.setTint(color: Int) {
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
}

fun Context.dpToPx(dp: Int) = (dp * Resources.getSystem().displayMetrics.density).toInt()