/*
 * Copyright © 2019 Marc Auberer. All rights reserved.
 */

package com.mrgames13.jimdo.colorconverter.tools

import android.content.res.ColorStateList
import android.widget.ImageView
import androidx.core.widget.ImageViewCompat

fun ImageView.setTint(color: Int) {
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
}