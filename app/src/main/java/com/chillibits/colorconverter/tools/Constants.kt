/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.colorconverter.tools

object Constants {
    // Request codes
    const val REQ_PICK_COLOR_FROM_IMAGE = 10001
    const val REQ_LOAD_COLOR = 10002
    const val REQ_INSTANT_INSTALL = 10003
    const val REQ_PERMISSIONS = 10004
    const val REQ_IMAGE_PICKER = 10005

    // Intent extra names
    const val EXTRA_ACTION = "action"
    const val EXTRA_CHOOSE_COLOR = "ChooseColor"
    const val EXTRA_IMAGE_URI = "ImageUri"
    const val EXTRA_COLOR = "Color"
    const val EXTRA_SELECTED_COLOR = "SelectedColor"

    // Other constants
    const val COLOR_ANIMATION_DURATION = 500L
    const val NAME_SELECTED_COLOR = "Selection"
    const val HSV_FORMAT_STRING = "%.02f"
}