package com.mrgames13.jimdo.colorconverter.RecyclerViewAdapters;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mrgames13.jimdo.colorconverter.App.ColorSelectionActivity;
import com.mrgames13.jimdo.colorconverter.CommonObjects.Color;
import com.mrgames13.jimdo.colorconverter.R;

import java.util.ArrayList;

public class ColorsAdapter extends RecyclerView.Adapter<ColorsAdapter.ViewHolderClass> {

    //Konstanten

    private ArrayList<Color> colors;

    //Variablen

    public ColorsAdapter(ArrayList<Color> colors) {
        //Variablen als Objekte
        this.colors = colors;
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {
        //Variablen als Objekte
        RelativeLayout item_container;
        AppCompatImageView item_color;
        TextView item_color_name;
        TextView item_color_values;

        private ViewHolderClass(View itemView) {
            super(itemView);
            //Oberflächenkomponenten initialisieren
            item_container = itemView.findViewById(R.id.item_container);
            item_color = itemView.findViewById(R.id.item_color);
            item_color_name = itemView.findViewById(R.id.item_color_name);
            item_color_values = itemView.findViewById(R.id.color_values);
        }
    }

    @Override
    public ViewHolderClass onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color, null);
        return new ViewHolderClass(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolderClass holder, final int pos) {
        //Daten befüllen
        final Color color = colors.get(pos);

        holder.item_color.setColorFilter(color.getColor());

        holder.item_color_name.setText(color.getName());

        String hex_red = Integer.toHexString(color.getRed()).toUpperCase();
        if(hex_red.length() < 2) hex_red = "0" + hex_red;
        String hex_green = Integer.toHexString(color.getGreen()).toUpperCase();
        if(hex_green.length() < 2) hex_green = "0" + hex_green;
        String hex_blue = Integer.toHexString(color.getBlue()).toUpperCase();
        if(hex_blue.length() < 2) hex_blue = "0" + hex_blue;

        float[] hsv = new float[3];
        android.graphics.Color.RGBToHSV(color.getRed(), color.getGreen(), color.getBlue(), hsv);

        String details_string = "RGB: " + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + " • HEX: #" + hex_red + hex_green + hex_blue + " • HSV: " + String.format("%.02f", hsv[0]) + ", " + String.format("%.02f", hsv[1]) + ", " + String.format("%.02f", hsv[2]);
        holder.item_color_values.setText(details_string);
        holder.item_color_values.setSelected(true);

        holder.item_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorSelectionActivity.own_instance.selectedColor(color);
            }
        });
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }
}