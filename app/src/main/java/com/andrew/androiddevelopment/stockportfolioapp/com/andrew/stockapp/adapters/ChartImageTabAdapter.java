package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrew.androiddevelopment.stockportfolioapp.MainNavigationScreen;
import com.andrew.androiddevelopment.stockportfolioapp.R;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.downloadertasks.ChartImageDownloaderTask;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.items.StockItem;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers.StockItemManager;

/**
 * Created by Andrew on 4/1/2015.
 */
public class ChartImageTabAdapter extends PagerAdapter {
    private Activity activity;

    private ImageView chart;
    private Bitmap[] chartImages;

    public ChartImageTabAdapter(Activity activity, Bitmap[] chartImages){
        this.activity = activity;
        this.chartImages = chartImages;
    }

    @Override
    public int getCount() {
        return 6;
    }

    /**
     * @return true if the value returned from {@link #instantiateItem(android.view.ViewGroup, int)} is the
     * same object as the {@link android.view.View} added to the {@link android.support.v4.view.ViewPager}.
     */
    @Override
    public boolean isViewFromObject(View view, Object o) {
        return o == view;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // Inflate a new layout from our resources
        View view = activity.getLayoutInflater().inflate(R.layout.chart_item,
                container, false);
        // Add the newly created View to the ViewPager
        container.addView(view);

        chart = (ImageView) view.findViewById(R.id.stockChartView);
        chart.setBackgroundResource(R.color.black);
        chart.setImageBitmap(chartImages[position]);

        return view;
    }

    /**
     * Destroy the item from the {@link android.support.v4.view.ViewPager}. In our case this is simply removing the
     * {@link View}.
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}