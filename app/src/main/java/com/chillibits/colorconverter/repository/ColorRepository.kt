/*
 * Copyright © Marc Auberer 2021. All rights reserved
 */

package com.chillibits.colorconverter.repository

import com.chillibits.colorconverter.storage.AppDatabase
import com.chillibits.colorconverter.storage.dbo.ColorDbo
import javax.inject.Inject

class ColorRepository @Inject constructor(db: AppDatabase) {

    // Variables as objects
    private val colorDao = db.colorDao()
    val colors = colorDao.getAll()

    fun getAll() = colorDao.getAll()
    fun insert(color: ColorDbo) = colorDao.insert(color)
    fun insert(colors: List<ColorDbo>) = colorDao.insert(colors)
    fun update(color: ColorDbo) = colorDao.update(color)
    fun delete(color: ColorDbo) = colorDao.delete(color)
}