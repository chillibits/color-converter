/*
 * Copyright © 2019 Marc Auberer. All rights reserved.
 */

package com.mrgames13.jimdo.colorconverter.tools

import android.content.Context
import com.mrgames13.jimdo.colorconverter.R
import com.mrgames13.jimdo.colorconverter.model.Color

class ColorNameTools(val context: Context) {

    private fun initColorList(): List<ColorName> {
        val c = context
        return mutableListOf(
            ColorName(c.getString(R.string.color_alice_blue), 0xF0, 0xF8, 0xFF),
            ColorName(c.getString(R.string.color_antique_white), 0xFA, 0xEB, 0xD7),
            ColorName(c.getString(R.string.color_aqua), 0x00, 0xFF, 0xFF),
            ColorName(c.getString(R.string.color_aquamarine), 0x7F, 0xFF, 0xD4),
            ColorName(c.getString(R.string.color_azure), 0xF0, 0xFF, 0xFF),
            ColorName(c.getString(R.string.color_beige), 0xF5, 0xF5, 0xDC),
            ColorName(c.getString(R.string.color_bisque), 0xFF, 0xE4, 0xC4),
            ColorName(c.getString(R.string.color_black), 0x00, 0x00, 0x00),
            ColorName(c.getString(R.string.color_blanched_almond), 0xFF, 0xEB, 0xCD),
            ColorName(c.getString(R.string.color_blue), 0x00, 0x00, 0xFF),
            ColorName(c.getString(R.string.color_blue_violet), 0x8A, 0x2B, 0xE2),
            ColorName(c.getString(R.string.color_brown), 0xA5, 0x2A, 0x2A),
            ColorName(c.getString(R.string.color_burly_wood), 0xDE, 0xB8, 0x87),
            ColorName(c.getString(R.string.color_cadet_blue), 0x5F, 0x9E, 0xA0),
            ColorName(c.getString(R.string.color_chartreuse), 0x7F, 0xFF, 0x00),
            ColorName(c.getString(R.string.color_chocolate), 0xD2, 0x69, 0x1E),
            ColorName(c.getString(R.string.color_coral), 0xFF, 0x7F, 0x50),
            ColorName(c.getString(R.string.color_cornflower_blue), 0x64, 0x95, 0xED),
            ColorName(c.getString(R.string.color_cornsilk), 0xFF, 0xF8, 0xDC),
            ColorName(c.getString(R.string.color_crimson), 0xDC, 0x14, 0x3C),
            ColorName(c.getString(R.string.color_cyan), 0x00, 0xFF, 0xFF),
            ColorName(c.getString(R.string.color_dark_blue), 0x00, 0x00, 0x8B),
            ColorName(c.getString(R.string.color_dark_cyan), 0x00, 0x8B, 0x8B),
            ColorName(c.getString(R.string.color_dark_golden_rod), 0xB8, 0x86, 0x0B),
            ColorName(c.getString(R.string.color_dark_gray), 0xA9, 0xA9, 0xA9),
            ColorName(c.getString(R.string.color_dark_green), 0x00, 0x64, 0x00),
            ColorName(c.getString(R.string.color_dark_khaki), 0xBD, 0xB7, 0x6B),
            ColorName(c.getString(R.string.color_dark_magenta), 0x8B, 0x00, 0x8B),
            ColorName(c.getString(R.string.color_dark_olive_green), 0x55, 0x6B, 0x2F),
            ColorName(c.getString(R.string.color_dark_orange), 0xFF, 0x8C, 0x00),
            ColorName(c.getString(R.string.color_dark_orchid), 0x99, 0x32, 0xCC),
            ColorName(c.getString(R.string.color_dark_red), 0x8B, 0x00, 0x00),
            ColorName(c.getString(R.string.color_dark_salmon), 0xE9, 0x96, 0x7A),
            ColorName(c.getString(R.string.color_dark_sea_green), 0x8F, 0xBC, 0x8F),
            ColorName(c.getString(R.string.color_dark_slate_blue), 0x48, 0x3D, 0x8B),
            ColorName(c.getString(R.string.color_dark_late_gray), 0x2F, 0x4F, 0x4F),
            ColorName(c.getString(R.string.color_dark_turquoise), 0x00, 0xCE, 0xD1),
            ColorName(c.getString(R.string.color_dark_violet), 0x94, 0x00, 0xD3),
            ColorName(c.getString(R.string.color_deep_pink), 0xFF, 0x14, 0x93),
            ColorName(c.getString(R.string.color_deep_sky_blue), 0x00, 0xBF, 0xFF),
            ColorName(c.getString(R.string.color_dim_gray), 0x69, 0x69, 0x69),
            ColorName(c.getString(R.string.color_dodger_blue), 0x1E, 0x90, 0xFF),
            ColorName(c.getString(R.string.color_fire_brick), 0xB2, 0x22, 0x22),
            ColorName(c.getString(R.string.color_floral_white), 0xFF, 0xFA, 0xF0),
            ColorName(c.getString(R.string.color_forest_green), 0x22, 0x8B, 0x22),
            ColorName(c.getString(R.string.color_fuchsia), 0xFF, 0x00, 0xFF),
            ColorName(c.getString(R.string.color_gainsboro), 0xDC, 0xDC, 0xDC),
            ColorName(c.getString(R.string.color_ghost_white), 0xF8, 0xF8, 0xFF),
            ColorName(c.getString(R.string.color_gold), 0xFF, 0xD7, 0x00),
            ColorName(c.getString(R.string.color_golden_rod), 0xDA, 0xA5, 0x20),
            ColorName(c.getString(R.string.color_gray), 0x80, 0x80, 0x80),
            ColorName(c.getString(R.string.color_green), 0x00, 0x80, 0x00),
            ColorName(c.getString(R.string.color_green_yellow), 0xAD, 0xFF, 0x2F),
            ColorName(c.getString(R.string.color_honey_dew), 0xF0, 0xFF, 0xF0),
            ColorName(c.getString(R.string.color_hot_pink), 0xFF, 0x69, 0xB4),
            ColorName(c.getString(R.string.color_indian_red), 0xCD, 0x5C, 0x5C),
            ColorName(c.getString(R.string.color_indigo), 0x4B, 0x00, 0x82),
            ColorName(c.getString(R.string.color_ivory), 0xFF, 0xFF, 0xF0),
            ColorName(c.getString(R.string.color_khaki), 0xF0, 0xE6, 0x8C),
            ColorName(c.getString(R.string.color_lavender), 0xE6, 0xE6, 0xFA),
            ColorName(c.getString(R.string.color_lavender_blush), 0xFF, 0xF0, 0xF5),
            ColorName(c.getString(R.string.color_lawn_green), 0x7C, 0xFC, 0x00),
            ColorName(c.getString(R.string.color_lemon_chiffon), 0xFF, 0xFA, 0xCD),
            ColorName(c.getString(R.string.color_light_blue), 0xAD, 0xD8, 0xE6),
            ColorName(c.getString(R.string.color_light_coral), 0xF0, 0x80, 0x80),
            ColorName(c.getString(R.string.color_light_cyan), 0xE0, 0xFF, 0xFF),
            ColorName(c.getString(R.string.color_light_golden_rod_yellow), 0xFA, 0xFA, 0xD2),
            ColorName(c.getString(R.string.color_light_gray), 0xD3, 0xD3, 0xD3),
            ColorName(c.getString(R.string.color_light_green), 0x90, 0xEE, 0x90),
            ColorName(c.getString(R.string.color_light_pink), 0xFF, 0xB6, 0xC1),
            ColorName(c.getString(R.string.color_light_salmon), 0xFF, 0xA0, 0x7A),
            ColorName(c.getString(R.string.color_light_sea_green), 0x20, 0xB2, 0xAA),
            ColorName(c.getString(R.string.color_light_sky_blue), 0x87, 0xCE, 0xFA),
            ColorName(c.getString(R.string.color_light_slate_gray), 0x77, 0x88, 0x99),
            ColorName(c.getString(R.string.color_light_steel_blue), 0xB0, 0xC4, 0xDE),
            ColorName(c.getString(R.string.color_light_yellow), 0xFF, 0xFF, 0xE0),
            ColorName(c.getString(R.string.color_lime), 0x00, 0xFF, 0x00),
            ColorName(c.getString(R.string.color_lime_green), 0x32, 0xCD, 0x32),
            ColorName(c.getString(R.string.color_linen), 0xFA, 0xF0, 0xE6),
            ColorName(c.getString(R.string.color_magenta), 0xFF, 0x00, 0xFF),
            ColorName(c.getString(R.string.color_maroon), 0x80, 0x00, 0x00),
            ColorName(c.getString(R.string.color_medium_aqua_marine), 0x66, 0xCD, 0xAA),
            ColorName(c.getString(R.string.color_medium_blue), 0x00, 0x00, 0xCD),
            ColorName(c.getString(R.string.color_medium_orchid), 0xBA, 0x55, 0xD3),
            ColorName(c.getString(R.string.color_medium_purple), 0x93, 0x70, 0xDB),
            ColorName(c.getString(R.string.color_medium_sea_green), 0x3C, 0xB3, 0x71),
            ColorName(c.getString(R.string.color_medium_slate_blue), 0x7B, 0x68, 0xEE),
            ColorName(c.getString(R.string.color_medium_spring_green), 0x00, 0xFA, 0x9A),
            ColorName(c.getString(R.string.color_medium_turquoise), 0x48, 0xD1, 0xCC),
            ColorName(c.getString(R.string.color_medium_violet_red), 0xC7, 0x15, 0x85),
            ColorName(c.getString(R.string.color_midnight_blue), 0x19, 0x19, 0x70),
            ColorName(c.getString(R.string.color_mint_cream), 0xF5, 0xFF, 0xFA),
            ColorName(c.getString(R.string.color_misty_rose), 0xFF, 0xE4, 0xE1),
            ColorName(c.getString(R.string.color_moccasin), 0xFF, 0xE4, 0xB5),
            ColorName(c.getString(R.string.color_navajo_white), 0xFF, 0xDE, 0xAD),
            ColorName(c.getString(R.string.color_navy), 0x00, 0x00, 0x80),
            ColorName(c.getString(R.string.color_old_lace), 0xFD, 0xF5, 0xE6),
            ColorName(c.getString(R.string.color_olive), 0x80, 0x80, 0x00),
            ColorName(c.getString(R.string.color_olive_drab), 0x6B, 0x8E, 0x23),
            ColorName(c.getString(R.string.color_orange), 0xFF, 0xA5, 0x00),
            ColorName(c.getString(R.string.color_orange_red), 0xFF, 0x45, 0x00),
            ColorName(c.getString(R.string.color_orchid), 0xDA, 0x70, 0xD6),
            ColorName(c.getString(R.string.color_pale_golden_rod), 0xEE, 0xE8, 0xAA),
            ColorName(c.getString(R.string.color_pale_green), 0x98, 0xFB, 0x98),
            ColorName(c.getString(R.string.color_pale_turquoise), 0xAF, 0xEE, 0xEE),
            ColorName(c.getString(R.string.color_pale_violet_red), 0xDB, 0x70, 0x93),
            ColorName(c.getString(R.string.color_papaya_whip), 0xFF, 0xEF, 0xD5),
            ColorName(c.getString(R.string.color_peach_puff), 0xFF, 0xDA, 0xB9),
            ColorName(c.getString(R.string.color_peru), 0xCD, 0x85, 0x3F),
            ColorName(c.getString(R.string.color_pink), 0xFF, 0xC0, 0xCB),
            ColorName(c.getString(R.string.color_plum), 0xDD, 0xA0, 0xDD),
            ColorName(c.getString(R.string.color_powder_blue), 0xB0, 0xE0, 0xE6),
            ColorName(c.getString(R.string.color_purple), 0x80, 0x00, 0x80),
            ColorName(c.getString(R.string.color_red), 0xFF, 0x00, 0x00),
            ColorName(c.getString(R.string.color_rosy_brown), 0xBC, 0x8F, 0x8F),
            ColorName(c.getString(R.string.color_royal_blue), 0x41, 0x69, 0xE1),
            ColorName(c.getString(R.string.color_saddle_brown), 0x8B, 0x45, 0x13),
            ColorName(c.getString(R.string.color_salmon), 0xFA, 0x80, 0x72),
            ColorName(c.getString(R.string.color_sandy_brown), 0xF4, 0xA4, 0x60),
            ColorName(c.getString(R.string.color_sea_green), 0x2E, 0x8B, 0x57),
            ColorName(c.getString(R.string.color_sea_shell), 0xFF, 0xF5, 0xEE),
            ColorName(c.getString(R.string.color_sienna), 0xA0, 0x52, 0x2D),
            ColorName(c.getString(R.string.color_silver), 0xC0, 0xC0, 0xC0),
            ColorName(c.getString(R.string.color_sky_blue), 0x87, 0xCE, 0xEB),
            ColorName(c.getString(R.string.color_slate_blue), 0x6A, 0x5A, 0xCD),
            ColorName(c.getString(R.string.color_slate_gray), 0x70, 0x80, 0x90),
            ColorName(c.getString(R.string.color_snow), 0xFF, 0xFA, 0xFA),
            ColorName(c.getString(R.string.color_spring_green), 0x00, 0xFF, 0x7F),
            ColorName(c.getString(R.string.color_steel_blue), 0x46, 0x82, 0xB4),
            ColorName(c.getString(R.string.color_tan), 0xD2, 0xB4, 0x8C),
            ColorName(c.getString(R.string.color_teal), 0x00, 0x80, 0x80),
            ColorName(c.getString(R.string.color_thistle), 0xD8, 0xBF, 0xD8),
            ColorName(c.getString(R.string.color_tomato), 0xFF, 0x63, 0x47),
            ColorName(c.getString(R.string.color_turquoise), 0x40, 0xE0, 0xD0),
            ColorName(c.getString(R.string.color_violet), 0xEE, 0x82, 0xEE),
            ColorName(c.getString(R.string.color_wheat), 0xF5, 0xDE, 0xB3),
            ColorName(c.getString(R.string.color_white), 0xFF, 0xFF, 0xFF),
            ColorName(c.getString(R.string.color_white_smoke), 0xF5, 0xF5, 0xF5),
            ColorName(c.getString(R.string.color_yellow), 0xFF, 0xFF, 0x00),
            ColorName(c.getString(R.string.color_yellow_green), 0x9A, 0xCD, 0x32)
        )
    }

    private fun getColorNameFromRgb(r: Int, g: Int, b: Int): String {
        val colorList = initColorList()
        var closestMatch: ColorName? = null
        var minMSE = Int.MAX_VALUE
        var mse: Int
        for (c in colorList) {
            mse = c.computeMSE(r, g, b)
            if (mse < minMSE) {
                minMSE = mse
                closestMatch = c
            }
        }
        return closestMatch?.getName() ?: "No color"
    }

    fun getColorNameFromColor(color: Color): String {
        return getColorNameFromRgb(color.red, color.green, color.blue)
    }
}