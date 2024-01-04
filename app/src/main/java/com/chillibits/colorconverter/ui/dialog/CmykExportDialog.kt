/*
 * Copyright © Marc Auberer 2017-2024. All rights reserved
 */

package com.chillibits.colorconverter.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import com.chillibits.colorconverter.shared.Constants
import com.chillibits.colorconverter.shared.copyTextToClipboard
import com.chillibits.colorconverter.tools.StorageTools
import com.mrgames13.jimdo.colorconverter.R

fun Context.showCmykExportDialog(st: StorageTools, cyan: Int, magenta: Int, yellow: Int, key: Int) {
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_export_cmyk, null)
    view.run {
        val dialog = AlertDialog.Builder(this@showCmykExportDialog)
            .setView(this)
            .show()

        val rememberSelection = findViewById<SwitchCompat>(R.id.rememberSelection)
        findViewById<LinearLayout>(R.id.formatCmyk).setOnClickListener {
            if (rememberSelection.isChecked) st.putBoolean(Constants.CMYK_REMEMBER_SELECTION, true)
            copyTextToClipboard(
                getString(R.string.cmyk_code), String.format(
                    getString(R.string.cmyk_clipboard),
                    cyan / 100.0, magenta / 100.0, yellow / 100.0, key / 100.0
                )
            )
            dialog.dismiss()
        }
        findViewById<LinearLayout>(R.id.formatCmykCss).setOnClickListener {
            if (rememberSelection.isChecked) st.putBoolean(Constants.CMYK_REMEMBER_SELECTION, false)
            copyTextToClipboard(
                getString(R.string.cmyk_code),
                String.format(getString(R.string.cmyk_clipboard_css), cyan, magenta, yellow, key)
            )
            dialog.dismiss()
        }
        rememberSelection.setOnCheckedChangeListener { _, isChecked ->
            st.putBoolean(Constants.CMYK_REMEMBER, isChecked)
        }
    }
}