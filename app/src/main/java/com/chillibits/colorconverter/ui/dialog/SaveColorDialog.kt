/*
 * Copyright © Marc Auberer 2017-2023. All rights reserved
 */

package com.chillibits.colorconverter.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import com.chillibits.colorconverter.tools.ColorNameTools
import com.chillibits.colorconverter.viewmodel.MainViewModel
import com.google.android.material.textfield.TextInputEditText
import com.mrgames13.jimdo.colorconverter.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun Context.showSaveColorDialog(cnt: ColorNameTools, vm: MainViewModel) {
    val defaultName = cnt.getColorNameFromColor(vm.selectedColor)

    // Initialize views
    val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_rename, null)
    val dialogName = dialogView.findViewById<TextInputEditText>(R.id.dialogName)
    dialogName.setText(defaultName)

    // Create dialog
    val dialog = AlertDialog.Builder(this)
        .setTitle(R.string.save_color)
        .setView(dialogView)
        .setNegativeButton(R.string.cancel, null)
        .setPositiveButton(R.string.save) { _, _ ->
            vm.selectedColor.name = dialogName.text.toString().trim()
            if (vm.selectedColor.name == defaultName) vm.selectedColor.name = ""
            vm.selectedColor.creationTimestamp = System.currentTimeMillis()
            // Insert color into local db
            CoroutineScope(Dispatchers.IO).launch { vm.insert() }
        }
        .show()

    // Prepare views
    dialogName.run {
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