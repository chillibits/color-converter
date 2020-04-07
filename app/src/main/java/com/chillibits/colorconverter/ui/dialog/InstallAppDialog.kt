/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.colorconverter.ui.dialog

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.chillibits.colorconverter.tools.Constants
import com.chillibits.colorconverter.ui.activity.MainActivity
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.github.javiersantos.materialstyleddialogs.enums.Style
import com.google.android.instantapps.InstantApps
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic
import com.mikepenz.iconics.utils.colorInt
import com.mrgames13.jimdo.colorconverter.R

fun Activity.showInstantAppInstallDialog(@StringRes message: Int) {
    MaterialStyledDialog.Builder(this)
        .setStyle(Style.HEADER_WITH_ICON)
        .setHeaderColorInt(ContextCompat.getColor(this, R.color.googlePlayHeaderColor))
        .setIcon(IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_google_play).apply {
            colorInt = Color.WHITE
        })
        .setTitle(R.string.install_app)
        .setDescription(message)
        .setPositiveText(R.string.install_app)
        .setNegativeText(R.string.cancel)
        .onPositive { _, _ ->
            Intent(this, MainActivity::class.java).run {
                putExtra(Constants.EXTRA_INSTANT_INSTALLED, true)
                InstantApps.showInstallPrompt(this@showInstantAppInstallDialog, this, Constants.REQ_INSTANT_INSTALL, "")
            }
        }
        .show()
}