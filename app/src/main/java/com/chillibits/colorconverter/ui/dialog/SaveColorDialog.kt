/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.colorconverter.ui.dialog

import android.content.Context
import android.text.InputType
import android.view.WindowManager
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import com.chillibits.colorconverter.model.Color
import com.chillibits.colorconverter.tools.ColorNameTools
import com.chillibits.colorconverter.tools.StorageTools
import com.mrgames13.jimdo.colorconverter.R

fun Context.showSaveColorDialog(cnt: ColorNameTools, st: StorageTools, selectedColor: Color) {
    // Initialize views
    val editTextName = EditText(this)
    editTextName.hint = getString(R.string.choose_name)
    editTextName.setText(cnt.getColorNameFromColor(selectedColor))
    editTextName.inputType = InputType.TYPE_TEXT_VARIATION_URI
    val container = FrameLayout(this)
    val containerParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
    containerParams.marginStart = resources.getDimensionPixelSize(R.dimen.dialog_margin)
    containerParams.marginEnd = resources.getDimensionPixelSize(R.dimen.dialog_margin)
    editTextName.layoutParams = containerParams
    container.addView(editTextName)

    // Create dialog
    val dialog = AlertDialog.Builder(this)
        .setTitle(R.string.save_color)
        .setView(container)
        .setNegativeButton(R.string.cancel, null)
        .setPositiveButton(R.string.save) { _, _ ->
            selectedColor.name = editTextName.text.toString().trim()
            st.addColor(selectedColor)
        }
        .show()

    // Prepare views
    editTextName.doAfterTextChanged {s ->
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = s.toString().isNotEmpty()
    }
    editTextName.selectAll()
    editTextName.requestFocus()
    dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
    dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
}