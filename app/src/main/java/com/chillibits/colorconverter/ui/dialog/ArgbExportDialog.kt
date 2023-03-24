/*
 * Copyright © Marc Auberer 2017-2023. All rights reserved
 */

package com.chillibits.colorconverter.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import com.chillibits.colorconverter.shared.Constants
import com.chillibits.colorconverter.shared.copyTextToClipboard
import com.chillibits.colorconverter.shared.round
import com.chillibits.colorconverter.tools.StorageTools
import com.mrgames13.jimdo.colorconverter.R

fun Context.showArgbExportDialog(st: StorageTools, alpha: Int, red: Int, green: Int, blue: Int) {
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_export_argb, null)
    view.run {
        val dialog = AlertDialog.Builder(this@showArgbExportDialog)
            .setView(this)
            .show()

        val rememberSelection = findViewById<SwitchCompat>(R.id.rememberSelection)
        findViewById<LinearLayout>(R.id.formatArgb).setOnClickListener {
            if (rememberSelection.isChecked) st.putBoolean(Constants.ARGB_REMEMBER_SELECTION, true)
            copyTextToClipboard(
                getString(R.string.argb_code),
                String.format(getString(R.string.argb_clipboard), alpha, red, green, blue)
            )
            dialog.dismiss()
        }
        findViewById<LinearLayout>(R.id.formatRgba).setOnClickListener {
            if (rememberSelection.isChecked) st.putBoolean(Constants.ARGB_REMEMBER_SELECTION, false)
            copyTextToClipboard(
                getString(R.string.argb_code),
                String.format(
                    getString(R.string.rgba_clipboard_css),
                    red,
                    green,
                    blue,
                    (alpha / 255.0).round(3)
                )
            )
            dialog.dismiss()
        }
        rememberSelection.setOnCheckedChangeListener { _, isChecked ->
            st.putBoolean(Constants.ARGB_REMEMBER, isChecked)
        }
    }
}