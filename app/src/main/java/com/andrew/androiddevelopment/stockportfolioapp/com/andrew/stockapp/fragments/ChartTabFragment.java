package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.fragments;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrew.androiddevelopment.stockportfolioapp.MainNavigationScreen;
import com.andrew.androiddevelopment.stockportfolioapp.R;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.downloadertasks.ChartImageDownloaderTask;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.items.PortfolioStockItem;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers.PortfolioManager;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.view.DepthPageTransformer;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.view.SlidingTabLayout;

/**
 * Created by Andrew on 4/1/2015.
 */
public class ChartTabFragment extends Fragment{
    int position;
    private PortfolioManager portfolioManager;
    public Bitmap[] chartImages = new Bitmap[8];
    private ChartImageDownloaderTask imageDownloader;
    private static final String CHART_ARRAY[] = new String[]{"1d","3d", "1w", "1m", "6m", "1y"};

    public ChartTabFragment(PortfolioManager portfolioManager, int position) {
        this.portfolioManager = portfolioManager;
        this.position = position;
    }

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_chart_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDividerColors(getActivity().getResources().getColor(R.color.delete_buttom));
        mSlidingTabLayout.setSelectedIndicatorColors(getActivity().getResources().getColor(R.color.bg_swipe_group_item_right));
        getStockChart();
    }

    private void getStockChart(){
        if(portfolioManager.getCount() > 0 ) {
            PortfolioStockItem stockSymbol = portfolioManager.getStockItem(position);
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
