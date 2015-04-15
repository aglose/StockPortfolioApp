package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.downloadertasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;

import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.adapters.ChartImageTabAdapter;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.view.SlidingTabLayout;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Andrew on 3/27/2015.
 */
public class ChartImageDownloaderTask extends AsyncTask<String, Void, Void>{
    private Bitmap[] chartImages;
    private ViewPager mViewPager;
    private Activity activity;
    private SlidingTabLayout mSlidingTabLayout;

    public ChartImageDownloaderTask(Activity activity, Bitmap[] chartImages, ViewPager mViewPager, SlidingTabLayout mSlidingTabLayout){
        this.chartImages = chartImages;
        this.mViewPager = mViewPager;
        this.activity = activity;
        this.mSlidingTabLayout = mSlidingTabLayout;
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
        mViewPager.setAdapter(new ChartImageTabAdapter(activity, chartImages));
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    public void getStockChartDrawable(String url, int i){
        InputStream stream = null;
        try {
            stream = (InputStream) new URL(url).getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap chart = BitmapFactory.decodeStream(stream);
        int x = 512;
        int y = 226;
        Bitmap result = Bitmap.createBitmap(chart, 0, 0, x, y);
        chartImages[i] = result;
    }
}
