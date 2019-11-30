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
            ColorName("Chartreuse", 0x7F, 0xFF, 0x00),
            ColorName("Chocolate", 0xD2, 0x69, 0x1E),
            ColorName("Coral", 0xFF, 0x7F, 0x50),
            ColorName("Cornflower Blue", 0x64, 0x95, 0xED),
            ColorName("Cornsilk", 0xFF, 0xF8, 0xDC),
            ColorName("Crimson", 0xDC, 0x14, 0x3C),
            ColorName("Cyan", 0x00, 0xFF, 0xFF),
            ColorName("Dark Blue", 0x00, 0x00, 0x8B),
            ColorName("Dark Cyan", 0x00, 0x8B, 0x8B),
            ColorName("Dark Golden Rod", 0xB8, 0x86, 0x0B),
            ColorName("Dark Gray", 0xA9, 0xA9, 0xA9),
            ColorName("Dark Green", 0x00, 0x64, 0x00),
            ColorName("Dark Khaki", 0xBD, 0xB7, 0x6B),
            ColorName("Dark Magenta", 0x8B, 0x00, 0x8B),
            ColorName("Dark Olive Green", 0x55, 0x6B, 0x2F),
            ColorName("Dark Orange", 0xFF, 0x8C, 0x00),
            ColorName("Dark Orchid", 0x99, 0x32, 0xCC),
            ColorName("Dark Red", 0x8B, 0x00, 0x00),
            ColorName("Dark Salmon", 0xE9, 0x96, 0x7A),
            ColorName("Dark Sea Green", 0x8F, 0xBC, 0x8F),
            ColorName("Dark Slate Blue", 0x48, 0x3D, 0x8B),
            ColorName("Dark Slate Gray", 0x2F, 0x4F, 0x4F),
            ColorName("Dark Turquoise", 0x00, 0xCE, 0xD1),
            ColorName("Dark Violet", 0x94, 0x00, 0xD3),
            ColorName("Deep Pink", 0xFF, 0x14, 0x93),
            ColorName("Deep Sky Blue", 0x00, 0xBF, 0xFF),
            ColorName("Dim Gray", 0x69, 0x69, 0x69),
            ColorName("Dodger Blue", 0x1E, 0x90, 0xFF),
            ColorName("Fire Brick", 0xB2, 0x22, 0x22),
            ColorName("Floral White", 0xFF, 0xFA, 0xF0),
            ColorName("Forest Green", 0x22, 0x8B, 0x22),
            ColorName("Fuchsia", 0xFF, 0x00, 0xFF),
            ColorName("Gainsboro", 0xDC, 0xDC, 0xDC),
            ColorName("Ghost White", 0xF8, 0xF8, 0xFF),
            ColorName("Gold", 0xFF, 0xD7, 0x00),
            ColorName("Golden Rod", 0xDA, 0xA5, 0x20),
            ColorName("Gray", 0x80, 0x80, 0x80),
            ColorName("Green", 0x00, 0x80, 0x00),
            ColorName("Green Yellow", 0xAD, 0xFF, 0x2F),
            ColorName("Honey Dew", 0xF0, 0xFF, 0xF0),
            ColorName("Hot Pink", 0xFF, 0x69, 0xB4),
            ColorName("Indian Red", 0xCD, 0x5C, 0x5C),
            ColorName("Indigo", 0x4B, 0x00, 0x82),
            ColorName("Ivory", 0xFF, 0xFF, 0xF0),
            ColorName("Khaki", 0xF0, 0xE6, 0x8C),
            ColorName("Lavender", 0xE6, 0xE6, 0xFA),
            ColorName("Lavender Blush", 0xFF, 0xF0, 0xF5),
            ColorName("Lawn Green", 0x7C, 0xFC, 0x00),
            ColorName("Lemon Chiffon", 0xFF, 0xFA, 0xCD),
            ColorName("Light Blue", 0xAD, 0xD8, 0xE6),
            ColorName("Light Coral", 0xF0, 0x80, 0x80),
            ColorName("Light Cyan", 0xE0, 0xFF, 0xFF),
            ColorName("Light Golden Rod Yellow", 0xFA, 0xFA, 0xD2),
            ColorName("Light Gray", 0xD3, 0xD3, 0xD3),
            ColorName("Light Green", 0x90, 0xEE, 0x90),
            ColorName("Light Pink", 0xFF, 0xB6, 0xC1),
            ColorName("Light Salmon", 0xFF, 0xA0, 0x7A),
            ColorName("Light Sea Green", 0x20, 0xB2, 0xAA),
            ColorName("Light Sky Blue", 0x87, 0xCE, 0xFA),
            ColorName("Light Slate Gray", 0x77, 0x88, 0x99),
            ColorName("Light Steel Blue", 0xB0, 0xC4, 0xDE),
            ColorName("Light Yellow", 0xFF, 0xFF, 0xE0),
            ColorName("Lime", 0x00, 0xFF, 0x00),
            ColorName("Lime Green", 0x32, 0xCD, 0x32),
            ColorName("Linen", 0xFA, 0xF0, 0xE6),
            ColorName("Magenta", 0xFF, 0x00, 0xFF),
            ColorName("Maroon", 0x80, 0x00, 0x00),
            ColorName("Medium Aqua Marine", 0x66, 0xCD, 0xAA),
            ColorName("Medium Blue", 0x00, 0x00, 0xCD),
            ColorName("Medium Orchid", 0xBA, 0x55, 0xD3),
            ColorName("Medium Purple", 0x93, 0x70, 0xDB),
            ColorName("Medium Sea Green", 0x3C, 0xB3, 0x71),
            ColorName("Medium Slate Blue", 0x7B, 0x68, 0xEE),
            ColorName("Medium Spring Green", 0x00, 0xFA, 0x9A),
            ColorName("Medium Turquoise", 0x48, 0xD1, 0xCC),
            ColorName("Medium Violet Red", 0xC7, 0x15, 0x85),
            ColorName("Midnight Blue", 0x19, 0x19, 0x70),
            ColorName("Mint Cream", 0xF5, 0xFF, 0xFA),
            ColorName("Misty Rose", 0xFF, 0xE4, 0xE1),
            ColorName("Moccasin", 0xFF, 0xE4, 0xB5),
            ColorName("Navajo White", 0xFF, 0xDE, 0xAD),
            ColorName("Navy", 0x00, 0x00, 0x80),
            ColorName("Old Lace", 0xFD, 0xF5, 0xE6),
            ColorName("Olive", 0x80, 0x80, 0x00),
            ColorName("Olive Drab", 0x6B, 0x8E, 0x23),
            ColorName("Orange", 0xFF, 0xA5, 0x00),
            ColorName("Orange Red", 0xFF, 0x45, 0x00),
            ColorName("Orchid", 0xDA, 0x70, 0xD6),
            ColorName("Pale Golden Rod", 0xEE, 0xE8, 0xAA),
            ColorName("Pale Green", 0x98, 0xFB, 0x98),
            ColorName("Pale Turquoise", 0xAF, 0xEE, 0xEE),
            ColorName("Pale Violet Red", 0xDB, 0x70, 0x93),
            ColorName("Papaya Whip", 0xFF, 0xEF, 0xD5),
            ColorName("Peach Puff", 0xFF, 0xDA, 0xB9),
            ColorName("Peru", 0xCD, 0x85, 0x3F),
            ColorName("Pink", 0xFF, 0xC0, 0xCB),
            ColorName("Plum", 0xDD, 0xA0, 0xDD),
            ColorName("Powder Blue", 0xB0, 0xE0, 0xE6),
            ColorName("Purple", 0x80, 0x00, 0x80),
            ColorName("Red", 0xFF, 0x00, 0x00),
            ColorName("Rosy Brown", 0xBC, 0x8F, 0x8F),
            ColorName("Royal Blue", 0x41, 0x69, 0xE1),
            ColorName("Saddle Brown", 0x8B, 0x45, 0x13),
            ColorName("Salmon", 0xFA, 0x80, 0x72),
            ColorName("Sandy Brown", 0xF4, 0xA4, 0x60),
            ColorName("Sea Green", 0x2E, 0x8B, 0x57),
            ColorName("Sea Shell", 0xFF, 0xF5, 0xEE),
            ColorName("Sienna", 0xA0, 0x52, 0x2D),
            ColorName("Silver", 0xC0, 0xC0, 0xC0),
            ColorName("Sky Blue", 0x87, 0xCE, 0xEB),
            ColorName("Slate Blue", 0x6A, 0x5A, 0xCD),
            ColorName("Slate Gray", 0x70, 0x80, 0x90),
            ColorName("Snow", 0xFF, 0xFA, 0xFA),
            ColorName("Spring Green", 0x00, 0xFF, 0x7F),
            ColorName("Steel Blue", 0x46, 0x82, 0xB4),
            ColorName("Tan", 0xD2, 0xB4, 0x8C),
            ColorName("Teal", 0x00, 0x80, 0x80),
            ColorName("Thistle", 0xD8, 0xBF, 0xD8),
            ColorName("Tomato", 0xFF, 0x63, 0x47),
            ColorName("Turquoise", 0x40, 0xE0, 0xD0),
            ColorName("Violet", 0xEE, 0x82, 0xEE),
            ColorName("Wheat", 0xF5, 0xDE, 0xB3),
            ColorName("White", 0xFF, 0xFF, 0xFF),
            ColorName("White Smoke", 0xF5, 0xF5, 0xF5),
            ColorName("Yellow", 0xFF, 0xFF, 0x00),
            ColorName("Yellow Green", 0x9A, 0xCD, 0x32)
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