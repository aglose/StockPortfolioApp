package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.androiddevelopment.stockportfolioapp.MainNavigationScreen;
import com.andrew.androiddevelopment.stockportfolioapp.R;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.CropChartImages;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.adapters.ChartImageTabAdapter;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.adapters.NewsCardAdapter;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.downloadertasks.ChartImageDownloaderTask;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.downloadertasks.StockNewsDownloaderTask;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.items.StockItem;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.listeners.OnSwipeTouchListener;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers.StockItemManager;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers.StockNewsManager;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.view.SlidingTabLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.io.IOException;

/**
 * Created by Andrew on 3/29/2015.
 */
public class MainStockNewsDisplayFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    private static final int NUM_PAGES = 5;

    private ProgressBar mLoadingView;
    private RecyclerView newsListRecycler;
    private NewsCardAdapter newsCardAdapter;

    private StockNewsDownloaderTask stockNewsDownloader;

    private StockNewsManager stockNewsManager;
    private StockItemManager stockItemManager;

    private ImageView imageView;
    private TextView chartTextView;
    private Switch aSwitch;

    private int imagePosition = 0;
    private int position;
    private int loadingAnimationDuration;
    private View rootView;

    private SlidingTabLayout mSlidingTabLayout;

    private ViewPager mViewPager;

    public MainStockNewsDisplayFragment newInstance(int sectionNumber, StockNewsManager stockNewsManager, StockItemManager stockItemManager) {
        MainStockNewsDisplayFragment fragment = new MainStockNewsDisplayFragment();
        fragment.setStockNewsManager(stockNewsManager);
        fragment.setStockItemManager(stockItemManager);

        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    public MainStockNewsDisplayFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main_navigation_screen, container, false);

//        imageView = (ImageView) rootView.findViewById(R.id.stockChartView);
//        chartTextView = (TextView) rootView.findViewById(R.id.chartTypeText);
//        applyTouchListener(imageView, chartTextView);

        aSwitch = (Switch) rootView.findViewById(R.id.switchNews);
        aSwitch.setChecked(true);
        aSwitch.setVisibility(View.INVISIBLE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        newsCardAdapter = new NewsCardAdapter(getActivity(), stockNewsManager);

        newsListRecycler = (RecyclerView) rootView.findViewById(R.id.newsList);
        newsListRecycler.setAdapter(newsCardAdapter);
//        newsListRecycler.setHasFixedSize(true);
        newsListRecycler.setLayoutManager(layoutManager);

        mLoadingView = (ProgressBar) rootView.findViewById(R.id.loading_spinner);

        newsListRecycler.setVisibility(View.GONE);

        loadingAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        position = getArguments().getInt(ARG_SECTION_NUMBER);
        if(position >= 0){
            while(position >= stockItemManager.getCount()){
                position--;
            }
            getStockNews(position, rootView);
        }
//
//        imageView.setBackgroundColor(R.color.black);
//        imageView.setImageBitmap(charts[0]);
//
//        chartTextView.setText("1d");
        return rootView;
    }


//    private void applyTouchListener(final ImageView imageView, final TextView chartTextView) {
//        imageView.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
//            @Override
//            public void onSwipeLeft() {
//                if(imagePosition < 6){
//                    imagePosition = ((imagePosition + 1 == 6) ? 0 : imagePosition + 1 % 5);
//                    imageView.setImageBitmap(chartImages[imagePosition]);
//                    chartTextView.setText(CHART_ARRAY[imagePosition]);
//                }
//            }
//            @Override
//            public void onSwipeRight() {
//                if(imagePosition >= 0){
//                    imagePosition--;
//                    if(imagePosition < 0){
//                        imagePosition = 5;
//                    }
//                    imageView.setImageBitmap(chartImages[imagePosition]);
//                    chartTextView.setText(CHART_ARRAY[imagePosition]);
//                }
//            }
//        });
//    }



    public void changeStockNews(boolean checked){
        aSwitch.setChecked(checked);
        getStockNews(position, rootView);
    }

    private void getStockNews(final int position, final View rootView){
        if(stockItemManager.getCount() > 0 ) {
            StockItem stockSymbol = stockItemManager.getStockItem(position);
            final String symbol = stockSymbol.getSymbol();
            String stockNewsURL = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20google.news%20where%20q%20%3D%20%22"+symbol+"%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

            if(((MainNavigationScreen)getActivity()).isNetworkActive()){
                stockNewsDownloader = new StockNewsDownloaderTask(getActivity(), rootView, symbol);
                String preference = "false";
                if(aSwitch.isChecked()){
                    preference = "true";
                }
                stockNewsDownloader.execute(stockNewsURL, preference);
            }
        }
    }

    public void crossfadeViews() {

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        newsListRecycler.setAlpha(0f);
        newsListRecycler.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        newsListRecycler.animate()
                .alpha(1f)
                .setDuration(loadingAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        mLoadingView.animate()
                .alpha(0f)
                .setDuration(loadingAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoadingView.setVisibility(View.GONE);
                    }
                });
    }

    public void setStockNewsManager(StockNewsManager stockNewsManager) {
        this.stockNewsManager = stockNewsManager;
    }

    public void setStockItemManager(StockItemManager stockItemManager) {
        this.stockItemManager = stockItemManager;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainNavigationScreen) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

}