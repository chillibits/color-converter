/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.colorconverter.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.chillibits.adobecolor.core.AdobeColorExporter
import com.chillibits.adobecolor.model.AdobeColor
import com.chillibits.colorconverter.storage.dbo.ColorDbo
import com.mrgames13.jimdo.colorconverter.R
import kotlinx.android.synthetic.main.dialog_export_palette.view.*

fun Context.showPaletteExportDialog(context: Context, colors: List<ColorDbo>) {
    val adobeColors = colors.map { AdobeColor(it.name, android.graphics.Color.rgb(it.red, it.green, it.blue)) }
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_export_palette, null)

    val dialog = AlertDialog.Builder(this)
        .setView(view)
        .show()

    view.exportACO.setOnClickListener {
        showRenameDialog(RenameDialogMode.EXPORT, object: OnRenameListener {
            override fun onRenameComplete(newName: String) {
                AdobeColorExporter(context).exportColorListAsACO(adobeColors)
            }
        })
        dialog.dismiss()
    }
    view.exportASE.setOnClickListener {
        showRenameDialog(RenameDialogMode.EXPORT, object: OnRenameListener {
            override fun onRenameComplete(name: String) {
                AdobeColorExporter(context).exportColorListAsASE(adobeColors, name)
            }
        })
        dialog.dismiss()
    }
    view.importACO.setOnClickListener {

        dialog.dismiss()
    }
    view.importASE.setOnClickListener {

        dialog.dismiss()
    }
}