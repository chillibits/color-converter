/*
 * Copyright © Marc Auberer 2017-2023. All rights reserved
 */

package com.chillibits.colorconverter.ui.dialog

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.github.javiersantos.materialstyleddialogs.enums.Style
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic
import com.mikepenz.iconics.utils.colorInt
import com.mrgames13.jimdo.colorconverter.R

fun Context.showTransparencyWarning() {
    MaterialStyledDialog.Builder(this)
        .setStyle(Style.HEADER_WITH_ICON)
        .setHeaderColorInt(ContextCompat.getColor(this, R.color.colorPrimary))
        .withIconAnimation(false)
        .setIcon(IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_colorize).apply {
            colorInt = Color.WHITE
        })
        .setTitle(R.string.transparency_warning_t)
        .setDescription(R.string.transparency_warning_m)
        .setPositiveText(R.string.ok)
        .show()
}