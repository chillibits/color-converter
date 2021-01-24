/*
 * Copyright © Marc Auberer 2017-2021. All rights reserved
 */

package com.chillibits.colorconverter.ui.dialog

import android.content.Context
import android.text.InputType
import android.view.WindowManager
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import com.chillibits.colorconverter.tools.ColorNameTools
import com.chillibits.colorconverter.viewmodel.MainViewModel
import com.mrgames13.jimdo.colorconverter.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun Context.showSaveColorDialog(cnt: ColorNameTools, vm: MainViewModel) {
    val defaultName = cnt.getColorNameFromColor(vm.selectedColor)

    // Initialize views
    val container = FrameLayout(this)
    val containerParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
        marginStart = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        marginEnd = resources.getDimensionPixelSize(R.dimen.dialog_margin)
    }
    val editTextName = EditText(this).apply {
        hint = getString(R.string.choose_name)
        setText(defaultName)
        inputType = InputType.TYPE_TEXT_VARIATION_URI
        layoutParams = containerParams
    }
    container.addView(editTextName)

    // Create dialog
    val dialog = AlertDialog.Builder(this)
        .setTitle(R.string.save_color)
        .setView(container)
        .setNegativeButton(R.string.cancel, null)
        .setPositiveButton(R.string.save) { _, _ ->
            vm.selectedColor.name = editTextName.text.toString().trim()
            if (vm.selectedColor.name == defaultName) vm.selectedColor.name = ""
            vm.selectedColor.creationTimestamp = System.currentTimeMillis()
            // Insert color into local db
            CoroutineScope(Dispatchers.IO).launch { vm.insert() }
        }
        .show()

    // Prepare views
    editTextName.run {
        doAfterTextChanged {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = it.toString().isNotEmpty()
        }
        selectAll()
        requestFocus()
    }
    dialog.window?.run {
        clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }
}