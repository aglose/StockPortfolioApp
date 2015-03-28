package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.downloadertasks;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Andrew on 3/27/2015.
 */
public class ChartImageDownloaderTask extends AsyncTask<String, Void, Void>{

    private static Drawable[] chartImages;
    private ImageLoaderListener listener;
    private ImageView imageView;

    public ChartImageDownloaderTask(Drawable[] chartImages, ImageLoaderListener listener, ImageView imageView){
        this.chartImages = chartImages;
        this.listener = listener;
        this.imageView = imageView;
    }

    @Override
    protected Void doInBackground(String... url) {
        String[] urls = url;
        int i = 0;
        while(i < urls.length){
            getStockChartDrawable(urls[i], i);
            i++;
        }

        return null;
    }
    @Override
    protected void onPostExecute(Void result) {
        imageView.setImageDrawable(chartImages[0]);
    }

    public interface ImageLoaderListener {
        void onImagesDownloaded(Drawable[] chartImages);
    }

    public static void getStockChartDrawable(String url, int i){


        InputStream stream = null;
        try {
            stream = (InputStream) new URL(url).getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable chart = Drawable.createFromStream(stream, "chart pic");
        chartImages[i] = chart;
    }
}
