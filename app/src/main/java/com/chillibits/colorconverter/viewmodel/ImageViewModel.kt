/*
 * Copyright © Marc Auberer 2017-2021. All rights reserved
 */

package com.chillibits.colorconverter.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Color
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.chillibits.colorconverter.tools.ColorTools
import com.mrgames13.jimdo.colorconverter.R
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    application: Application,
    private val ct: ColorTools
): AndroidViewModel(application) {

    // Variables as objects
    private val context = application
    lateinit var tts: TextToSpeech
    var valueSelectedColor: Int = Color.BLACK
    var valueVibrantColor: Int = Color.BLACK
    var valueVibrantColorLight: Int = Color.BLACK
    var valueVibrantColorDark: Int = Color.BLACK
    var valueMutedColor: Int = Color.BLACK
    var valueMutedColorLight: Int = Color.BLACK
    var valueMutedColorDark: Int = Color.BLACK
    var imageUri: String? = null

    // Variables
    var initialized = false

    init {
        // Initialize tts
        initializeTTS()
    }

    fun computeVibrantColors(bitmap: Bitmap) {
        valueVibrantColor = ct.getVibrantColor(bitmap)
        valueVibrantColorLight = ct.getLightVibrantColor(bitmap)
        valueVibrantColorDark = ct.getDarkVibrantColor(bitmap)
        valueMutedColor = ct.getMutedColor(bitmap)
        valueMutedColorLight = ct.getLightMutedColor(bitmap)
        valueMutedColorDark = ct.getDarkMutedColor(bitmap)
    }

    private fun initializeTTS() {
        // Initialize tts
        tts = TextToSpeech(context) { status ->
            if(status == TextToSpeech.SUCCESS) {
                val result = tts.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(context, R.string.language_not_available, Toast.LENGTH_SHORT).show()
                } else initialized = true
            } else Toast.makeText(context, R.string.initialization_failed, Toast.LENGTH_SHORT).show()
        }
    }
}