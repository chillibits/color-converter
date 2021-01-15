/*
 * Copyright © Marc Auberer 2017-2021. All rights reserved
 */

package com.chillibits.colorconverter.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.chillibits.colorconverter.shared.Constants
import com.chillibits.colorconverter.shared.copyTextToClipboard
import com.chillibits.colorconverter.tools.StorageTools
import com.mrgames13.jimdo.colorconverter.R
import kotlinx.android.synthetic.main.dialog_export_cmyk.view.*

fun Context.showCmykExportDialog(st: StorageTools, cyan: Int, magenta: Int, yellow: Int, key: Int) {
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_export_cmyk, null)

    val dialog = AlertDialog.Builder(this)
        .setView(view)
        .show()

    view.formatCmyk.setOnClickListener {
        if(view.rememberSelection.isChecked) st.putBoolean(Constants.CMYK_REMEMBER_SELECTION, true)
        copyTextToClipboard(getString(R.string.cmyk_code), String.format(getString(R.string.cmyk_clipboard),
            cyan / 100.0, magenta / 100.0, yellow / 100.0, key / 100.0))
        dialog.dismiss()
    }
    view.formatCmykCss.setOnClickListener {
        if(view.rememberSelection.isChecked) st.putBoolean(Constants.CMYK_REMEMBER_SELECTION, false)
        copyTextToClipboard(getString(R.string.cmyk_code),
            String.format(getString(R.string.cmyk_clipboard_css), cyan, magenta, yellow, key))
        dialog.dismiss()
    }
    view.rememberSelection.setOnCheckedChangeListener { _, isChecked ->
        st.putBoolean(Constants.CMYK_REMEMBER, isChecked)
    }
}