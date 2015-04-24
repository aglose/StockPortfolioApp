package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.andrew.androiddevelopment.stockportfolioapp.MainNavigationScreen;
import com.andrew.androiddevelopment.stockportfolioapp.R;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.adapters.StockNewsCardAdapter;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.downloadertasks.StockNewsDownloaderTask;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.items.PortfolioStockItem;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers.PortfolioManager;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers.StockNewsManager;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.view.SlidingTabLayout;

/**
 * Created by Andrew on 3/29/2015.
 */
public class StockNewsDisplayFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    private static final int NUM_PAGES = 5;

    private ProgressBar mLoadingView;
    private RecyclerView newsListRecycler;
    private StockNewsCardAdapter stockNewsCardAdapter;

    private StockNewsDownloaderTask stockNewsDownloader;

    private StockNewsManager stockNewsManager;
    private PortfolioManager portfolioManager;

    private ToggleButton switchNewsButton;
    private ToggleButton chartToggleButton;

    private int position;
    private int loadingAnimationDuration;
    private View rootView;

    private SlidingTabLayout mSlidingTabLayout;

    private ViewPager mViewPager;

    public StockNewsDisplayFragment newInstance(int sectionNumber, StockNewsManager stockNewsManager, PortfolioManager portfolioManager) {
        StockNewsDisplayFragment fragment = new StockNewsDisplayFragment();
        fragment.setStockNewsManager(stockNewsManager);
        fragment.setPortfolioManager(portfolioManager);

        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    public StockNewsDisplayFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_stock_news_screen, container, false);

        switchNewsButton = (ToggleButton) rootView.findViewById(R.id.switchNewsButton);
        chartToggleButton = (ToggleButton) rootView.findViewById(R.id.chartToggleButton);

        switchNewsButton.setChecked(true);
        switchNewsButton.setVisibility(View.INVISIBLE);
        chartToggleButton.setVisibility(View.INVISIBLE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        stockNewsCardAdapter = new StockNewsCardAdapter(getActivity(), stockNewsManager);

        newsListRecycler = (RecyclerView) rootView.findViewById(R.id.newsList);
        newsListRecycler.setAdapter(stockNewsCardAdapter);
//        newsListRecycler.setHasFixedSize(true);
        newsListRecycler.setLayoutManager(layoutManager);

        mLoadingView = (ProgressBar) rootView.findViewById(R.id.loading_spinner);

        newsListRecycler.setVisibility(View.GONE);

        loadingAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        position = getArguments().getInt(ARG_SECTION_NUMBER);
        if(position >= 0){
            while(position >= portfolioManager.getCount()){
                position--;
            }
            getStockNews(position, rootView);
        }

        return rootView;
    }

    public void changeStockNews(boolean checked){
        switchNewsButton.setChecked(checked);
        getStockNews(position, rootView);
    }

    private void getStockNews(final int position, final View rootView){
        if(portfolioManager.getCount() > 0 ) {
            PortfolioStockItem stockSymbol = portfolioManager.getStockItem(position);
            final String symbol = stockSymbol.getSymbol();
            String stockNewsURL = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20google.news%20where%20q%20%3D%20%22"+symbol+"%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

            if(((MainNavigationScreen)getActivity()).isNetworkActive()){
                stockNewsDownloader = new StockNewsDownloaderTask(getActivity(), rootView, symbol);
                String preference = "false";
                if(switchNewsButton.isChecked()){
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
        chartToggleButton.setVisibility(View.VISIBLE);
        switchNewsButton.setVisibility(View.VISIBLE);

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

    public void setPortfolioManager(PortfolioManager portfolioManager) {
        this.portfolioManager = portfolioManager;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainNavigationScreen) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

}