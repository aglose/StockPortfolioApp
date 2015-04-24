package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp;

import android.content.Context;

import com.andrew.androiddevelopment.stockportfolioapp.R;

/**
 * Created by Andrew on 4/3/2015.
 */
public class IconUtility {

    public static String decideIcon(Context context, String sourceName){
        if(sourceName.contains(context.getString(R.string.foxbusiness)))
            return context.getString(R.string.foxbusiness);
        if(sourceName.contains(context.getString(R.string.cnn)))
            return context.getString(R.string.cnn);
        if(sourceName.contains(context.getString(R.string.cnn)))
            return context.getString(R.string.cnn);
        if(sourceName.contains(context.getString(R.string.cnn)))
            return context.getString(R.string.cnn);
        if(sourceName.contains(context.getString(R.string.cnn)))
            return context.getString(R.string.cnn);
        if(sourceName.contains(context.getString(R.string.cnn)))
            return context.getString(R.string.cnn);
        if(sourceName.contains(context.getString(R.string.cnn)))
            return context.getString(R.string.cnn);
        if(sourceName.contains(context.getString(R.string.cnn)))
            return context.getString(R.string.cnn);

        return null;
    }
}
