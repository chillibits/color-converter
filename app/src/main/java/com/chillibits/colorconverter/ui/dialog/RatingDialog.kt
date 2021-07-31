/*
 * Copyright © Marc Auberer 2017-2021. All rights reserved
 */

package com.chillibits.colorconverter.ui.dialog

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.chillibits.simplesettings.tool.openGooglePlayAppSite
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.github.javiersantos.materialstyleddialogs.enums.Style
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic
import com.mikepenz.iconics.utils.colorInt
import com.mrgames13.jimdo.colorconverter.R

fun AppCompatActivity.showRatingDialog() {
    MaterialStyledDialog.Builder(this)
        .setStyle(Style.HEADER_WITH_ICON)
        .setHeaderColorInt(ContextCompat.getColor(this, R.color.googlePlayHeaderColor))
        .withIconAnimation(false)
        .setIcon(IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_google_play).apply {
            colorInt = Color.WHITE
        })
        .setTitle(R.string.rate)
        .setDescription(R.string.rate_m)
        .setPositiveText(R.string.rate)
        .setNegativeText(R.string.cancel)
        .onPositive { openGooglePlayAppSite() }
        .show()
}