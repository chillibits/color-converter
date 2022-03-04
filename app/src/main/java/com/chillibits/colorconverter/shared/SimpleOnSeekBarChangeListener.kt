/*
 * Copyright © Marc Auberer 2017-2022. All rights reserved
 */

package com.chillibits.colorconverter.shared

import android.widget.SeekBar

open class SimpleOnSeekBarChangeListener : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
}