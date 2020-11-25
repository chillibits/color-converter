/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.colorconverter.shared

import android.content.Context
import com.chillibits.colorconverter.tools.ClipboardTools
import com.chillibits.colorconverter.tools.ColorNameTools
import com.chillibits.colorconverter.tools.ColorTools
import com.chillibits.colorconverter.tools.StorageTools
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@InstallIn(ActivityComponent::class)
@Module
class HiltInstanceProviders {
    @Provides
    fun provideStorageTools(@ActivityContext context: Context) = StorageTools(context)

    @Provides
    fun provideColorTools(@ActivityContext context: Context) = ColorTools(context)

    @Provides
    fun provideColorNameTools(@ActivityContext context: Context) = ColorNameTools(context)

    @Provides
    fun provideClipboardTools(@ActivityContext context: Context, st: StorageTools, ct: ColorTools) =
        ClipboardTools(context, st, ct)
}