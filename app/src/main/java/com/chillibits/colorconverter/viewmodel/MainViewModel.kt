/*
 * Copyright © Marc Auberer 2017-2024. All rights reserved
 */

package com.chillibits.colorconverter.viewmodel

import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.chillibits.colorconverter.model.Color
import com.chillibits.colorconverter.repository.ColorRepository
import com.chillibits.colorconverter.shared.Constants
import com.chillibits.colorconverter.shared.toDbo
import com.chillibits.colorconverter.tools.ColorNameTools
import com.google.android.instantapps.InstantApps
import com.mrgames13.jimdo.colorconverter.R
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val repository: ColorRepository,
    private val cnt: ColorNameTools
) : AndroidViewModel(application) {

    // Variables as objects
    private val context = application
    var selectedColor = Color(0, Constants.NAME_SELECTED_COLOR, android.graphics.Color.BLACK, -1)
    lateinit var tts: TextToSpeech

    // Variables
    val isInstant = InstantApps.isInstantApp(context)
    var initialized = false
    var showTransparencyWarning = false

    init {
        // Initialize tts, if the app does not run in instant mode
        if (!isInstant) initializeTTS()
    }

    fun insert() = repository.insert(selectedColor.toDbo())

    private fun initializeTTS() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(context, R.string.language_not_available, Toast.LENGTH_SHORT)
                        .show()
                } else initialized = true
            } else Toast.makeText(context, R.string.initialization_failed, Toast.LENGTH_SHORT)
                .show()
        }
    }

    @Suppress("DEPRECATION")
    fun speakColor() {
        when {
            isAudioMuted() -> Toast.makeText(context, R.string.audio_muted, Toast.LENGTH_SHORT)
                .show()
            initialized -> {
                val colorName = cnt.getColorNameFromColor(selectedColor)
                tts.speak(colorName, TextToSpeech.QUEUE_FLUSH, null, null)
            }
            else -> Toast.makeText(context, R.string.initialization_failed, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun isAudioMuted(): Boolean {
        val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return manager.ringerMode != AudioManager.RINGER_MODE_NORMAL
    }
}