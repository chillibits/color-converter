/*
 * Copyright © Marc Auberer 2017-2022. All rights reserved
 */

package com.chillibits.colorconverter.shared

import android.content.Context
import androidx.room.Room
import com.chillibits.colorconverter.storage.AppDatabase
import com.chillibits.colorconverter.storage.MIGRATION_2_3
import com.chillibits.colorconverter.tools.ClipboardTools
import com.chillibits.colorconverter.tools.ColorNameTools
import com.chillibits.colorconverter.tools.ColorTools
import com.chillibits.colorconverter.tools.StorageTools
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext

@InstallIn(ActivityComponent::class)
@Module
object HiltInstanceProvidersActivity {
    @Provides
    fun provideDatabase(@ActivityContext context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, Constants.DB_NAME)
            .addMigrations(MIGRATION_2_3).build()

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

@InstallIn(ViewModelComponent::class)
@Module
object HiltInstanceProvidersViewModel {
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, Constants.DB_NAME)
            .addMigrations(MIGRATION_2_3).build()

    @Provides
    fun provideColorTools(@ApplicationContext context: Context) = ColorTools(context)

    @Provides
    fun provideColorNameTools(@ApplicationContext context: Context) = ColorNameTools(context)
}