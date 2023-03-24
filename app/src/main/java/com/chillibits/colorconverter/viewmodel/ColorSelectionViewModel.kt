/*
 * Copyright © Marc Auberer 2017-2023. All rights reserved
 */

package com.chillibits.colorconverter.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.chillibits.colorconverter.model.Color
import com.chillibits.colorconverter.repository.ColorRepository
import com.chillibits.colorconverter.shared.toDbo
import com.chillibits.colorconverter.shared.toObj
import com.chillibits.colorconverter.storage.dbo.ColorDbo
import com.chillibits.colorconverter.tools.ColorNameTools
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ColorSelectionViewModel @Inject constructor(
    application: Application,
    private val cnt: ColorNameTools,
    private val repository: ColorRepository
): AndroidViewModel(application) {

    // Variables as objects
    val colors: LiveData<List<ColorDbo>> = repository.getAll().map { colors ->
        colors.forEach { color ->
            color.name = color.name.ifEmpty { cnt.getColorNameFromColor(color.toObj()) }
        }
        colors
    }
    var selectedColor: Color? = null

    fun insert(colors: List<Color>) = repository.insert(colors.map {
        ColorDbo(it.id, it.name, it.alpha, it.red, it.green, it.blue, it.creationTimestamp)
    })
    fun update() = selectedColor?.run { repository.update(this.toDbo()) }
    fun delete() {
        selectedColor?.run { repository.delete(this.toDbo()) }
        selectedColor = null
    }
}