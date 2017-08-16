package com.mrgames13.jimdo.colorconverter.Utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.graphics.Palette;

import com.mrgames13.jimdo.colorconverter.R;

public class ColorUtils {
    //Konstanten

    //Variablen als Objekte
    private Resources res;

    //Variablen

    public ColorUtils(Resources res) {
        this.res = res;
    }

    public int getVibrantColor(Bitmap image) throws Exception {
        Palette palette = Palette.from(image).generate();
        return palette.getVibrantColor(res.getColor(R.color.grey));
    }

    public int getLightVibrantColor(Bitmap image) throws Exception {
        Palette palette = Palette.from(image).generate();
        return palette.getLightVibrantColor(res.getColor(R.color.grey));
    }

    public int getDarkVibrantColor(Bitmap image) throws Exception {
        Palette palette = Palette.from(image).generate();
        return palette.getDarkVibrantColor(res.getColor(R.color.grey));
    }

    public int getMutedColor(Bitmap image) throws Exception {
        Palette palette = Palette.from(image).generate();
        return palette.getMutedColor(res.getColor(R.color.grey));
    }

    public int getLightMutedColor(Bitmap image) throws Exception {
        Palette palette = Palette.from(image).generate();
        return palette.getLightMutedColor(res.getColor(R.color.grey));
    }

    public int getDarkMutedColor(Bitmap image) throws Exception {
        Palette palette = Palette.from(image).generate();
        return palette.getDarkMutedColor(res.getColor(R.color.grey));
    }

    public int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

    public int addTransparency(int color) {
        int alpha = Math.round(Color.alpha(color) * 0.6f);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    public int getComplimentary(int color) {
        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int blue = Color.blue(color);
        int green = Color.green(color);

        red = (~red) & 0xff;
        blue = (~blue) & 0xff;
        green = (~green) & 0xff;

        return Color.argb(alpha, red, green, blue);
    }
}