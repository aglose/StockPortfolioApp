package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.fragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrew.androiddevelopment.stockportfolioapp.MainNavigationScreen;
import com.andrew.androiddevelopment.stockportfolioapp.R;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.adapters.ChartImageTabAdapter;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.downloadertasks.ChartImageDownloaderTask;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.items.StockItem;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers.StockItemManager;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.view.SlidingTabLayout;

/**
 * Created by Andrew on 4/1/2015.
 */
public class ChartTabFragment extends Fragment{
    int position;
    private StockItemManager stockItemManager;
    public Bitmap[] chartImages = new Bitmap[8];
    private ChartImageDownloaderTask imageDownloader;
    private static final String CHART_ARRAY[] = new String[]{"1d","3d", "1w", "1m", "6m", "1y"};

    public ChartTabFragment(StockItemManager stockItemManager, int position) {
        this.stockItemManager = stockItemManager;
        this.position = position;
    }

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.chart_slide_tab_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDividerColors(getActivity().getResources().getColor(R.color.delete_buttom));
        mSlidingTabLayout.setSelectedIndicatorColors(getActivity().getResources().getColor(R.color.bg_swipe_group_item_right));
        getStockChart();
    }

    private void getStockChart(){
        if(stockItemManager.getCount() > 0 ) {
            StockItem stockSymbol = stockItemManager.getStockItem(position);
            String urls[] = new String[6];
            for (int i = 0; i < 6; i++) {
                String stockURL = "https://chart.yahoo.com/z?t=" + CHART_ARRAY[i] + "&s=" + stockSymbol.getSymbol();
                Log.d("Debug stock", stockURL);
                urls[i] = stockURL;
            }
            if(((MainNavigationScreen)getActivity()).isNetworkActive()){
                imageDownloader = new ChartImageDownloaderTask(getActivity(), chartImages, mViewPager, mSlidingTabLayout);
                imageDownloader.execute(urls);
            }
        }
    }






}
