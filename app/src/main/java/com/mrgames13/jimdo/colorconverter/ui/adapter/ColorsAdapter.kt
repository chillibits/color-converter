package com.mrgames13.jimdo.colorconverter.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mrgames13.jimdo.colorconverter.R
import com.mrgames13.jimdo.colorconverter.model.Color
import com.mrgames13.jimdo.colorconverter.tools.setTint
import com.mrgames13.jimdo.colorconverter.ui.activity.ColorSelectionActivity
import kotlinx.android.synthetic.main.item_color.view.*

class ColorsAdapter(private val activity: ColorSelectionActivity, private val colors: ArrayList<Color>) : RecyclerView.Adapter<ColorsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorsAdapter.ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_color, null)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = colors.size

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val color = colors[pos]

        holder.itemView.item_color.setTint(color.color)
        holder.itemView.item_color_name.text = color.name

        holder.itemView.setOnClickListener {
            activity.selectedColor(color)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}