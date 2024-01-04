/*
 * Copyright © Marc Auberer 2017-2024. All rights reserved
 */

package com.chillibits.colorconverter.ui.dialog

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.github.javiersantos.materialstyleddialogs.enums.Style
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic
import com.mikepenz.iconics.utils.colorInt
import com.mrgames13.jimdo.colorconverter.R

fun Context.showRecommendationDialog() {
    MaterialStyledDialog.Builder(this)
        .setStyle(Style.HEADER_WITH_ICON)
        .setHeaderColorInt(ContextCompat.getColor(this, R.color.colorPrimary))
        .withIconAnimation(false)
        .setIcon(IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_share).apply {
            colorInt = Color.WHITE
        })
        .setTitle(R.string.share)
        .setDescription(R.string.share_m)
        .setPositiveText(R.string.share)
        .setNegativeText(R.string.cancel)
        .onPositive {
            startActivity(Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, getString(R.string.recommend_string))
                type = "text/plain"
            })
        }
        .show()
}