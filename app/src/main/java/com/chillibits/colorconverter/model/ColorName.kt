/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.colorconverter.model

class ColorName(
    var name: String,
    var r: Int,
    var g: Int,
    var b: Int
) {
    fun computeMSE(pixR: Int, pixG: Int, pixB: Int) =
        ((pixR - r) * (pixR - r) + (pixG - g) * (pixG - g) + (pixB - b) * (pixB - b)) / 3
}