/*
 * Copyright © Marc Auberer 2017-2023. All rights reserved
 */

package com.chillibits.colorconverter.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chillibits.colorconverter.model.Color
import com.chillibits.colorconverter.tools.ColorNameTools
import com.chillibits.colorconverter.tools.ColorTools
import com.chillibits.colorconverter.ui.adapter.ColorsAdapter
import com.mrgames13.jimdo.colorconverter.R

fun Context.showColorPaletteDialog(
    listener: ColorsAdapter.ColorSelectionListener,
    cnt: ColorNameTools,
    ct: ColorTools
) {
    // Get color list
    val timestamp = System.currentTimeMillis()
    val colors = cnt.getColorList()
        .map { Color(-1, it.name, 255, it.r, it.g, it.b, timestamp) }
        .sortedBy { it.name }

    // Show dialog
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_color_palette, null)
    val d = AlertDialog.Builder(this)
        .setTitle(R.string.color_palette)
        .setView(view)
        .setNegativeButton(R.string.cancel, null)
        .show()

    // Fill dialog with data
    view.findViewById<RecyclerView>(R.id.palette).apply {
        adapter = ColorsAdapter(
            this@showColorPaletteDialog,
            object : ColorsAdapter.ColorSelectionListener {
                override fun onColorSelected(color: Color) {
                    listener.onColorSelected(color)
                    d.dismiss()
                }
            },
            ct,
            colors
        )
        layoutManager = LinearLayoutManager(this@showColorPaletteDialog)
        setHasFixedSize(true)
    }
}