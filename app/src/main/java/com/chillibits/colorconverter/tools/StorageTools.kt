/*
 * Copyright © Marc Auberer 2017-2023. All rights reserved
 */

package com.chillibits.colorconverter.tools

import android.content.Context
import com.chillibits.simplesettings.tool.getPrefs
import javax.inject.Inject

class StorageTools @Inject constructor(val context: Context) {

    fun putBoolean(name: String, value: Boolean) =
        context.getPrefs().edit().putBoolean(name, value).apply()

    fun putInt(name: String, value: Int) = context.getPrefs().edit().putInt(name, value).apply()
}