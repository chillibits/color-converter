/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.colorconverter.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chillibits.colorconverter.model.Color
import com.chillibits.colorconverter.tools.ColorTools
import com.chillibits.colorconverter.tools.Constants
import com.chillibits.colorconverter.tools.StorageTools
import com.chillibits.colorconverter.tools.setTint
import com.chillibits.colorconverter.ui.activity.ColorSelectionActivity
import com.mrgames13.jimdo.colorconverter.R
import kotlinx.android.synthetic.main.item_color.view.*

class ColorsAdapter(
    private val activity: ColorSelectionActivity,
    private val colors: ArrayList<Color>
): RecyclerView.Adapter<ColorsAdapter.ViewHolder>() {

    private val ct = ColorTools(activity)
    private val st = StorageTools(activity)
    private val isAlphaDisabled = st.getBoolean(Constants.DISABLE_ALPHA, false)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(activity).inflate(R.layout.item_color, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = colors.size

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.run {
            val color = colors[pos]

            itemView.itemColor.setTint(color.color)
            itemView.itemColorName.text = color.name

            val hsv = FloatArray(3)
            android.graphics.Color.RGBToHSV(color.red, color.green, color.blue, hsv)
            val cmyk = ct.getCmykFromRgb(color.red, color.green, color.blue)

            itemView.itemColorValues.text = if(isAlphaDisabled)
                String.format(
                    activity.getString(R.string.color_summary_alpha_disabled),
                    color.red, color.green, color.blue,
                    "%06X".format(0xFFFFFF and color.color).toUpperCase(),
                    String.format(Constants.HSV_FORMAT_STRING, hsv[0]),
                    String.format(Constants.HSV_FORMAT_STRING, hsv[1]),
                    String.format(Constants.HSV_FORMAT_STRING, hsv[2]),
                    cmyk[0], cmyk[1], cmyk[2], cmyk[3]
                )
            else
                String.format(
                    activity.getString(R.string.color_summary),
                    color.alpha, color.red, color.green, color.blue,
                    "%08X".format(color.color).toUpperCase(),
                    String.format(Constants.HSV_FORMAT_STRING, hsv[0]),
                    String.format(Constants.HSV_FORMAT_STRING, hsv[1]),
                    String.format(Constants.HSV_FORMAT_STRING, hsv[2]),
                    cmyk[0], cmyk[1], cmyk[2], cmyk[3]
                )

            itemView.isSelected = true

            itemView.setOnClickListener { activity.selectedColor(color) }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}