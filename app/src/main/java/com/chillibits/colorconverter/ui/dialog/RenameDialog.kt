/*
 * Copyright © Marc Auberer 2017-2022. All rights reserved
 */

package com.chillibits.colorconverter.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.mrgames13.jimdo.colorconverter.R

interface OnRenameListener {
    fun onRenameComplete(newName: String)
    fun onRenameCancel() {}
}

enum class RenameDialogMode {
    RENAME,
    EXPORT
}

fun Context.showRenameDialog(mode: RenameDialogMode, listener: OnRenameListener, oldName: String = "") {
    val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_rename, null)
    val newName = dialogView.findViewById<TextInputEditText>(R.id.dialogName)
    newName.setText(oldName)

    val dialogTitle = if (mode == RenameDialogMode.RENAME) R.string.rename else R.string.export

    // Create dialog
    val dialog = AlertDialog.Builder(this)
        .setTitle(dialogTitle)
        .setView(dialogView)
        .setPositiveButton(dialogTitle) { _, _ ->
            val name = newName.text.toString().trim()
            if (name.isNotEmpty()) listener.onRenameComplete(name)
        }
        .setNegativeButton(R.string.cancel) { _, _ -> listener.onRenameCancel()}
        .show()

    // Prepare views
    newName.doAfterTextChanged {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = newName.toString().isNotEmpty()
    }
    newName.selectAll()
    newName.requestFocus()
    dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
    dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
}