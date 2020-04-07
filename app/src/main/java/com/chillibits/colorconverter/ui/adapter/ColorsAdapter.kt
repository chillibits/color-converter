/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.colorconverter.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chillibits.colorconverter.model.Color
import com.chillibits.colorconverter.tools.Constants
import com.chillibits.colorconverter.tools.setTint
import com.chillibits.colorconverter.ui.activity.ColorSelectionActivity
import com.mrgames13.jimdo.colorconverter.R
import kotlinx.android.synthetic.main.item_color.view.*

class ColorsAdapter(private val activity: ColorSelectionActivity, private val colors: ArrayList<Color>) : RecyclerView.Adapter<ColorsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(activity).inflate(R.layout.item_color, parent, false)
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

            itemView.itemColorValues.text = String.format(
                activity.getString(R.string.color_summary),
                color.red, color.green,
                color.blue,
                String.format(Constants.HEX_FORMAT_STRING, 0xFFFFFF and color.color),
                String.format(Constants.HSV_FORMAT_STRING, hsv[0]),
                String.format(Constants.HSV_FORMAT_STRING, hsv[1]),
                String.format(Constants.HSV_FORMAT_STRING, hsv[2])
            )
            itemView.isSelected = true

            itemView.setOnClickListener { activity.selectedColor(color) }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}