/*
 * Copyright © Marc Auberer 2017-2023. All rights reserved
 */

package com.chillibits.colorconverter.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.chillibits.colorconverter.model.Color
import com.chillibits.colorconverter.shared.Constants
import com.chillibits.colorconverter.shared.setTint
import com.chillibits.colorconverter.tools.ColorTools
import com.chillibits.simplesettings.tool.getPrefBooleanValue
import com.mrgames13.jimdo.colorconverter.R
import java.util.*
import javax.inject.Inject

class ColorsAdapter @Inject constructor(
    private val context: Context,
    private val listener: ColorSelectionListener,
    private val ct: ColorTools,
    private var colors: List<Color> = emptyList()
): RecyclerView.Adapter<ColorsAdapter.ViewHolder>() {

    // Variables
    private val isAlphaDisabled = context.getPrefBooleanValue(Constants.ENABLE_ALPHA, true)

    // Interfaces
    interface ColorSelectionListener {
        fun onColorSelected(color: Color)
    }

    fun updateData(data: List<Color>) {
        this.colors = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_color, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = colors.size

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.itemView.run {
            val color = colors[pos]

            findViewById<AppCompatImageView>(R.id.itemColor).setTint(color.color)
            findViewById<TextView>(R.id.itemColorName).text = color.name

            val hsv = FloatArray(3)
            android.graphics.Color.RGBToHSV(color.red, color.green, color.blue, hsv)
            val cmyk = ct.getCmykFromRgb(color.red, color.green, color.blue)

            findViewById<TextView>(R.id.itemColorValues).text = if(isAlphaDisabled)
                String.format(
                    context.getString(R.string.color_summary_alpha_disabled),
                    color.red, color.green, color.blue,
                    "%06X".format(0xFFFFFF and color.color).uppercase(Locale.getDefault()),
                    String.format(Constants.HSV_FORMAT_STRING, hsv[0]),
                    String.format(Constants.HSV_FORMAT_STRING, hsv[1]),
                    String.format(Constants.HSV_FORMAT_STRING, hsv[2]),
                    cmyk[0], cmyk[1], cmyk[2], cmyk[3]
                )
            else
                String.format(
                    context.getString(R.string.color_summary),
                    color.alpha, color.red, color.green, color.blue,
                    "%08X".format(color.color).uppercase(Locale.getDefault()),
                    String.format(Constants.HSV_FORMAT_STRING, hsv[0]),
                    String.format(Constants.HSV_FORMAT_STRING, hsv[1]),
                    String.format(Constants.HSV_FORMAT_STRING, hsv[2]),
                    cmyk[0], cmyk[1], cmyk[2], cmyk[3]
                )

            isSelected = true

            setOnClickListener { listener.onColorSelected(color) }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}