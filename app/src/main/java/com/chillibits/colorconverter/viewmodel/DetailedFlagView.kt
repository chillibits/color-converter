/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.colorconverter.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.graphics.*
import com.mrgames13.jimdo.colorconverter.R
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.flag.FlagView
import kotlinx.android.synthetic.main.flag_layout.view.*

@SuppressLint("ViewConstructor")
class DetailedFlagView(context: Context, layout: Int) : FlagView(context, layout) {
    override fun onRefresh(colorEnvelope: ColorEnvelope?) {
        flag_color.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(colorEnvelope!!.color, BlendModeCompat.SRC_IN)
        flag_color_rgb.text = String.format(context.getString(R.string.rgb_), colorEnvelope.color.red, colorEnvelope.color.green, colorEnvelope.color.blue)
        flag_color_hex.text = String.format(context.getString(R.string.hex_), String.format("#%06X", 0xFFFFFF and colorEnvelope.color))
        val hsv = FloatArray(3)
        android.graphics.Color.RGBToHSV(colorEnvelope.color.red, colorEnvelope.color.green, colorEnvelope.color.blue, hsv)
        flag_color_hsv.text = String.format(context.getString(R.string.hsv_), String.format("%.02f", hsv[0]), String.format("%.02f", hsv[1]), String.format("%.02f", hsv[2]))
    }
}