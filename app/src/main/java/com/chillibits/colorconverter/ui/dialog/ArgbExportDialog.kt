/*
 * Copyright © Marc Auberer 2017-2021. All rights reserved
 */

package com.chillibits.colorconverter.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.chillibits.colorconverter.shared.Constants
import com.chillibits.colorconverter.shared.copyTextToClipboard
import com.chillibits.colorconverter.shared.round
import com.chillibits.colorconverter.tools.StorageTools
import com.mrgames13.jimdo.colorconverter.R
import kotlinx.android.synthetic.main.dialog_export_argb.view.*

fun Context.showArgbExportDialog(st: StorageTools, alpha: Int, red: Int, green: Int, blue: Int) {
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_export_argb, null)

    val dialog = AlertDialog.Builder(this)
        .setView(view)
        .show()

    view.formatArgb.setOnClickListener {
        if(view.rememberSelection.isChecked) st.putBoolean(Constants.ARGB_REMEMBER_SELECTION, true)
        copyTextToClipboard(getString(R.string.argb_code),
            String.format(getString(R.string.argb_clipboard), alpha, red, green, blue))
        dialog.dismiss()
    }
    view.formatRgba.setOnClickListener {
        if(view.rememberSelection.isChecked) st.putBoolean(Constants.ARGB_REMEMBER_SELECTION, false)
        copyTextToClipboard(getString(R.string.argb_code),
            String.format(getString(R.string.rgba_clipboard_css), red, green, blue, (alpha / 255.0).round(3)))
        dialog.dismiss()
    }
    view.rememberSelection.setOnCheckedChangeListener { _, isChecked ->
        st.putBoolean(Constants.ARGB_REMEMBER, isChecked)
    }
}