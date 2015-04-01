package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

/**
 * Created by Andrew on 3/31/2015.
 */
public class CropChartImages implements Transformation {
    @Override public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());
        int x = 512;
        int y = 226;
        Bitmap result = Bitmap.createBitmap(source, 0, 0, x, y);
        if (result != source) {
            source.recycle();
        }
        return result;
    }

    @Override public String key() { return "square()"; }
}