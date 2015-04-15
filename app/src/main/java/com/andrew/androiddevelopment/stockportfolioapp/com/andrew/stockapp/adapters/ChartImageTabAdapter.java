package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.andrew.androiddevelopment.stockportfolioapp.R;

/**
 * Created by Andrew on 4/1/2015.
 */
public class ChartImageTabAdapter extends PagerAdapter {
    private Activity activity;
    private Animator mCurrentAnimator;

    private int mShortAnimationDuration;

    private ImageView chartSmall;
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
    public Object instantiateItem(ViewGroup container, final int position) {
        // Inflate a new layout from our resources
        final View rootView = activity.getLayoutInflater().inflate(R.layout.chart_item,
                container, false);
        // Add the newly created View to the ViewPager
        container.addView(rootView);

        rootView.setTranslationX(-1 * rootView.getWidth() * position);
        chartSmall = (ImageView) rootView.findViewById(R.id.stockChartView);
        chartSmall.setBackgroundResource(R.color.black);
        chartSmall.setImageBitmap(chartImages[position]);

        return rootView;
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