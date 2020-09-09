/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.colorconverter.tools

class ColorName(
    var name: String,
    private var r: Int,
    private var g: Int,
    private var b: Int
) {
    fun computeMSE(pixR: Int, pixG: Int, pixB: Int) =
        ((pixR - r) * (pixR - r) + (pixG - g) * (pixG - g) + (pixB - b) * (pixB - b)) / 3
}