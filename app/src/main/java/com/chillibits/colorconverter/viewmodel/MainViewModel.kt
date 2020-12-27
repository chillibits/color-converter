/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.colorconverter.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.chillibits.colorconverter.model.Color
import com.chillibits.colorconverter.repository.ColorRepository
import com.chillibits.colorconverter.shared.Constants
import com.chillibits.colorconverter.storage.AppDatabase
import com.chillibits.colorconverter.tools.StorageTools
import com.google.android.instantapps.InstantApps
import com.mrgames13.jimdo.colorconverter.R
import java.util.*

class MainViewModel @ViewModelInject constructor(
    application: Application,
    private val repository: ColorRepository,
    private val db: AppDatabase,
    private val st: StorageTools,
    @Assisted private val savedStateHandle: SavedStateHandle
): AndroidViewModel(application) {

    // Variables as objects
    private val context = application
    var selectedColor = Color(0, Constants.NAME_SELECTED_COLOR, android.graphics.Color.BLACK, -1)
    lateinit var tts: TextToSpeech

    // Variables
    var initialized = false
    var showTransparencyWarning = false
    var isAlphaDisabled = st.getBoolean(Constants.DISABLE_ALPHA)

    init {
        // Initialize tts, if the app does not run in instant mode
        if (!InstantApps.isInstantApp(context)) initializeTTS()
    }

    private fun initializeTTS() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(context, R.string.language_not_available, Toast.LENGTH_SHORT).show()
                } else initialized = true
            } else Toast.makeText(context, R.string.initialization_failed, Toast.LENGTH_SHORT).show()
        }
    }
}