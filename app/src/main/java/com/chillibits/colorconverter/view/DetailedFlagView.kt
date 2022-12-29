/*
 * Copyright © Marc Auberer 2017-2022. All rights reserved
 */

package com.chillibits.colorconverter.view

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.*
import com.chillibits.colorconverter.shared.Constants
import com.chillibits.simplesettings.tool.getPrefBooleanValue
import com.mrgames13.jimdo.colorconverter.R
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.flag.FlagView
import java.util.*

@SuppressLint("ViewConstructor")
class DetailedFlagView(context: Context, layout: Int) : FlagView(context, layout) {

    private val isAlphaDisabled = context.getPrefBooleanValue(Constants.ENABLE_ALPHA, true)

    override fun onRefresh(envelope: ColorEnvelope?) {
        val flagColor = findViewById<AppCompatImageView>(R.id.flagColor);
        val flagColorArgb = findViewById<TextView>(R.id.flagColorArgb);
        val flagColorHex = findViewById<TextView>(R.id.flagColorHex);
        val flagColorHsv = findViewById<TextView>(R.id.flagColorHsv);

        flagColor.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(envelope!!.color, BlendModeCompat.SRC_IN)
        flagColorArgb.text = if(isAlphaDisabled) {
            String.format(context.getString(R.string.rgb_), envelope.color.red, envelope.color.green, envelope.color.blue)
        } else {
            String.format(context.getString(R.string.argb_), envelope.color.alpha, envelope.color.red, envelope.color.green, envelope.color.blue)
        }
        flagColorHex.text = if(isAlphaDisabled)
            String.format(context.getString(R.string.hex_),
                "%06X".format(0xFFFFFF and envelope.color).uppercase(Locale.getDefault())
            )
        else
            String.format(context.getString(R.string.hex_),
                "%08X".format(envelope.color).uppercase(Locale.getDefault())
            )
        val hsv = FloatArray(3)
        android.graphics.Color.RGBToHSV(envelope.color.red, envelope.color.green, envelope.color.blue, hsv)
        flagColorHsv.text = String.format(context.getString(R.string.hsv_), String.format(Constants.HSV_FORMAT_STRING, hsv[0]), String.format(
            Constants.HSV_FORMAT_STRING, hsv[1]), String.format(Constants.HSV_FORMAT_STRING, hsv[2]))
    }
}