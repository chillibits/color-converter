/*
 * Copyright © 2019 Marc Auberer. All rights reserved.
 */

package com.mrgames13.jimdo.colorconverter

import android.app.Application
import android.content.Context
import com.google.android.play.core.splitcompat.SplitCompat

class ColorConverter : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        SplitCompat.install(this)
    }
}