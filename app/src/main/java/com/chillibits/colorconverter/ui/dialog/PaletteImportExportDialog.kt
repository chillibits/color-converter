/*
 * Copyright © Marc Auberer 2017-2021. All rights reserved
 */

package com.chillibits.colorconverter.ui.dialog

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.chillibits.adobecolor.core.AdobeColorTool
import com.chillibits.adobecolor.model.AdobeColor
import com.chillibits.colorconverter.model.Color
import com.chillibits.colorconverter.storage.dbo.ColorDbo
import com.chillibits.colorconverter.viewmodel.ColorSelectionViewModel
import com.mrgames13.jimdo.colorconverter.R
import kotlinx.android.synthetic.main.dialog_export_palette.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun Context.showPaletteImportExportDialog(
    activity: AppCompatActivity,
    vm: ColorSelectionViewModel,
    colors: List<ColorDbo>
) {
    val colorTool = AdobeColorTool(activity)
    val importTimestamp = System.currentTimeMillis()
    val importListener = object: AdobeColorTool.AdobeImportListener {
        override fun onComplete(groups: Map<String, List<AdobeColor>>) {
            val importedColors = ArrayList<Color>()
            CoroutineScope(Dispatchers.IO).launch {
                for (group in groups) {
                    importedColors.addAll(group.value.map {
                        Log.d("CC", it.color.red.toString())
                        Log.d("CC", it.color.green.toString())
                        Log.d("CC", it.color.blue.toString())
                        Color(0, it.name, it.color, importTimestamp)
                    })
                }
                vm.insert(importedColors)
            }
        }

        override fun onError(e: Exception) {
            e.printStackTrace()
            Toast.makeText(activity, R.string.error, Toast.LENGTH_SHORT).show()
        }
    }

    val adobeColors = colors.map { AdobeColor(android.graphics.Color.rgb(it.red, it.green, it.blue), it.name) }
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_export_palette, null)

    val dialog = AlertDialog.Builder(this)
        .setView(view)
        .show()

    view.exportACO.setOnClickListener {
        showRenameDialog(RenameDialogMode.EXPORT, object: OnRenameListener {
            override fun onRenameComplete(newName: String) {
                colorTool.exportColorListAsACO(adobeColors, newName)
            }
        })
        dialog.dismiss()
    }
    view.exportASE.setOnClickListener {
        showRenameDialog(RenameDialogMode.EXPORT, object: OnRenameListener {
            override fun onRenameComplete(newName: String) {
                colorTool.exportColorListAsASE(adobeColors, newName)
            }
        })
        dialog.dismiss()
    }
    view.importACO.setOnClickListener {
        colorTool.importColorList(activity, importListener)
        dialog.dismiss()
    }
    view.importASE.setOnClickListener {
        colorTool.importColorList(activity, importListener)
        dialog.dismiss()
    }
}