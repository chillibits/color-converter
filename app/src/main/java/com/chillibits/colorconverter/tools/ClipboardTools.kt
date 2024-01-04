/*
 * Copyright © Marc Auberer 2017-2024. All rights reserved
 */

package com.chillibits.colorconverter.tools

import android.content.Context
import com.chillibits.colorconverter.model.Color
import com.chillibits.colorconverter.shared.Constants
import com.chillibits.colorconverter.shared.copyTextToClipboard
import com.chillibits.colorconverter.shared.round
import com.chillibits.colorconverter.ui.dialog.showArgbExportDialog
import com.chillibits.colorconverter.ui.dialog.showCmykExportDialog
import com.chillibits.simplesettings.tool.getPrefBooleanValue
import com.mrgames13.jimdo.colorconverter.R
import java.util.*

class ClipboardTools(
    private val context: Context,
    private val st: StorageTools,
    private val ct: ColorTools
) {

    fun copyNameToClipboard(name: String) =
        context.copyTextToClipboard(context.getString(R.string.name), name)

    fun copyArgbToClipboard(color: Color) = context.run {
        if (getPrefBooleanValue(Constants.ENABLE_ALPHA, true)) {
            // Show multiple choice dialog
            if (!getPrefBooleanValue(Constants.ARGB_REMEMBER, false)) {
                showArgbExportDialog(st, color.alpha, color.red, color.green, color.blue)
            } else if (getPrefBooleanValue(Constants.ARGB_REMEMBER_SELECTION, false)) {
                copyTextToClipboard(
                    getString(R.string.argb_code), String.format(
                        getString(R.string.argb_clipboard),
                        color.alpha, color.red, color.green, color.blue
                    )
                )
            } else {
                copyTextToClipboard(
                    getString(R.string.argb_code), String.format(
                        getString(R.string.rgba_clipboard_css),
                        color.red, color.green, color.blue, (color.alpha / 255.0).round(3)
                    )
                )
            }
        } else {
            copyTextToClipboard(
                getString(R.string.rgb_code), String.format(
                    getString(R.string.rgb_clipboard),
                    color.red, color.green, color.blue
                )
            )
        }
    }

    fun copyHexToClipboard(color: Color) = context.run {
        copyTextToClipboard(
            getString(R.string.hex_code),
            if (getPrefBooleanValue(Constants.ENABLE_ALPHA, true))
                "#%08X".format(color.color).uppercase(Locale.getDefault())
            else
                "#%06X".format(0xFFFFFF and color.color).uppercase(Locale.getDefault())
        )
    }

    fun copyHsvToClipboard(color: Color) = context.run {
        val hsv = FloatArray(3)
        android.graphics.Color.RGBToHSV(color.red, color.green, color.blue, hsv)
        val hsvString = String.format(
            getString(R.string.hsv_),
            String.format(Constants.HSV_FORMAT_STRING, hsv[0]),
            String.format(Constants.HSV_FORMAT_STRING, hsv[1]),
            String.format(Constants.HSV_FORMAT_STRING, hsv[2])
        )
        copyTextToClipboard(getString(R.string.hsv_clipboard), hsvString)
    }

    fun copyCMYKToClipboard(color: Color) = context.run {
        // Show multiple choice dialog
        val cmyk = ct.getCmykFromRgb(color.red, color.green, color.blue)
        if (!getPrefBooleanValue(Constants.CMYK_REMEMBER, false)) {
            showCmykExportDialog(st, cmyk[0], cmyk[1], cmyk[2], cmyk[3])
        } else if (getPrefBooleanValue(Constants.CMYK_REMEMBER_SELECTION, false)) {
            copyTextToClipboard(
                getString(R.string.cmyk_code), String.format(
                    getString(R.string.cmyk_clipboard),
                    cmyk[0] / 100.0, cmyk[1] / 100.0, cmyk[2] / 100.0, cmyk[3] / 100.0
                )
            )
        } else {
            copyTextToClipboard(
                getString(R.string.cmyk_code), String.format(
                    getString(R.string.cmyk_clipboard_css),
                    cmyk[0], cmyk[1], cmyk[2], cmyk[3]
                )
            )
        }
    }
}