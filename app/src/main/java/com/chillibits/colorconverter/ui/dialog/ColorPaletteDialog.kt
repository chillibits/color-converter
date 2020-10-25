/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.colorconverter.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.chillibits.colorconverter.model.Color
import com.chillibits.colorconverter.tools.ColorNameTools
import com.chillibits.colorconverter.ui.adapter.ColorsAdapter
import com.mrgames13.jimdo.colorconverter.R
import kotlinx.android.synthetic.main.dialog_color_palette.view.*

fun Context.showColorPaletteDialog(listener: ColorsAdapter.ColorSelectionListener) {
    // Get color list
    val timestamp = System.currentTimeMillis()
    val colors = ColorNameTools(this).getColorList()
        .map { Color(-1, it.name, 255, it.r, it.g, it.b, timestamp) }

    // Show dialog
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_color_palette, null)
    view.palette.apply {
        adapter = ColorsAdapter(this@showColorPaletteDialog, colors, listener)
        layoutManager = LinearLayoutManager(this@showColorPaletteDialog)
        setHasFixedSize(true)
    }

    AlertDialog.Builder(this)
        .setView(view)
        .show()
}