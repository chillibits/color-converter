/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.colorconverter.shared

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.ImageViewCompat
import com.chillibits.colorconverter.model.Color
import com.chillibits.colorconverter.storage.dbo.ColorDbo
import com.mrgames13.jimdo.colorconverter.R

fun ImageView.setTint(color: Int) =
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))

fun dpToPx(dp: Int) = (dp * Resources.getSystem().displayMetrics.density).toInt()

fun Context.copyTextToClipboard(key: String, value: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(key, value))
    Toast.makeText(this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}

fun Color.toDbo() = ColorDbo(id, name, alpha, red, green, blue, creationTimestamp)
fun ColorDbo.toObj() = Color(id, name, alpha, red, green, blue, creationTimestamp)