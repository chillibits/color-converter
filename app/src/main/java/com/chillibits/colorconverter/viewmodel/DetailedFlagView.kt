/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.colorconverter.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.graphics.*
import com.chillibits.colorconverter.shared.Constants
import com.chillibits.colorconverter.tools.StorageTools
import com.mrgames13.jimdo.colorconverter.R
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.flag.FlagView
import kotlinx.android.synthetic.main.flag_layout.view.*
import java.util.*

@SuppressLint("ViewConstructor")
class DetailedFlagView(context: Context, layout: Int) : FlagView(context, layout) {

    private val st = StorageTools(context)
    private val isAlphaDisabled = st.getBoolean(Constants.DISABLE_ALPHA, false)

    override fun onRefresh(envelope: ColorEnvelope?) {
        flagColor.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(envelope!!.color, BlendModeCompat.SRC_IN)
        flagColorArgb.text = if(isAlphaDisabled) {
            String.format(context.getString(R.string.rgb_), envelope.color.red, envelope.color.green, envelope.color.blue)
        } else {
            String.format(context.getString(R.string.argb_), envelope.color.alpha, envelope.color.red, envelope.color.green, envelope.color.blue)
        }
        flagColorHex.text = if(isAlphaDisabled)
            String.format(context.getString(R.string.hex_), "%06X".format(0xFFFFFF and envelope.color).toUpperCase(Locale.getDefault()))
        else
            String.format(context.getString(R.string.hex_), "%08X".format(envelope.color).toUpperCase(Locale.getDefault()))
        val hsv = FloatArray(3)
        android.graphics.Color.RGBToHSV(envelope.color.red, envelope.color.green, envelope.color.blue, hsv)
        flagColorHsv.text = String.format(context.getString(R.string.hsv_), String.format(Constants.HSV_FORMAT_STRING, hsv[0]), String.format(
            Constants.HSV_FORMAT_STRING, hsv[1]), String.format(Constants.HSV_FORMAT_STRING, hsv[2]))
    }
}