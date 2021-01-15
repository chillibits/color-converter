/*
 * Copyright © Marc Auberer 2017-2021. All rights reserved
 */

package com.chillibits.colorconverter.tools

import android.content.Context
import com.chillibits.colorconverter.shared.Constants
import javax.inject.Inject

class StorageTools @Inject constructor(val context: Context) {

    // Variables as objects
    private val prefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun putBoolean(name: String, value: Boolean) = prefs.edit().putBoolean(name, value).apply()
    fun getBoolean(name: String, default: Boolean = false) = prefs.getBoolean(name, default)
    fun putInt(name: String, value: Int) = prefs.edit().putInt(name, value).apply()
    fun getInt(name: String, default: Int = 0) = prefs.getInt(name, default)
}