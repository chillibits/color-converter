package com.mrgames13.jimdo.colorconverter.Utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.palette.graphics.Palette;

import com.mrgames13.jimdo.colorconverter.R;

public class ColorUtils {
    //Konstanten

    //Variablen als Objekte
    private Resources res;

    //Variablen

    public ColorUtils(Resources res) {
        this.res = res;
    }

    public int getVibrantColor(Bitmap image) {
        Palette palette = Palette.from(image).generate();
        return palette.getVibrantColor(res.getColor(R.color.grey));
    }

    public int getLightVibrantColor(Bitmap image) {
        Palette palette = Palette.from(image).generate();
        return palette.getLightVibrantColor(res.getColor(R.color.grey));
    }

    public int getDarkVibrantColor(Bitmap image) {
        Palette palette = Palette.from(image).generate();
        return palette.getDarkVibrantColor(res.getColor(R.color.grey));
    }

    public int getMutedColor(Bitmap image) {
        Palette palette = Palette.from(image).generate();
        return palette.getMutedColor(res.getColor(R.color.grey));
    }

    public int getLightMutedColor(Bitmap image) {
        Palette palette = Palette.from(image).generate();
        return palette.getLightMutedColor(res.getColor(R.color.grey));
    }

    public int getDarkMutedColor(Bitmap image) {
        Palette palette = Palette.from(image).generate();
        return palette.getDarkMutedColor(res.getColor(R.color.grey));
    }

    public int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

    public int getTextColor(int color) {
        int sum = Color.red(color) + Color.green(color) + Color.blue(color);
        if(sum > 384) return Color.BLACK;
        return Color.WHITE;
    }

}