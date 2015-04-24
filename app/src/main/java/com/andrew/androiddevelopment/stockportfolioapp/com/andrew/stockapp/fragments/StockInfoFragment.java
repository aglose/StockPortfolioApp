package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrew.androiddevelopment.stockportfolioapp.R;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers.PortfolioManager;

/**
 * Created by Andrew on 4/23/2015.
 */
public class StockInfoFragment extends Fragment{
    private PortfolioManager portfolioManager;
    private int position;
    private TextView currentPrice;
    private TextView percentChange;
    private TextView openingPrice;
    private TextView volume;

    public Fragment newInstance(PortfolioManager portfolioManager, int i) {
        StockInfoFragment stockInfoFragment = new StockInfoFragment();
        stockInfoFragment.setPortfolioManager(portfolioManager);
        stockInfoFragment.setPosition(i);

        return stockInfoFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_stock_info, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        currentPrice = (TextView) view.findViewById(R.id.currentPrice);
        percentChange = (TextView) view.findViewById(R.id.percentChange);
        openingPrice = (TextView) view.findViewById(R.id.openingPrice);
        volume = (TextView) view.findViewById(R.id.volume);

        currentPrice.setText(portfolioManager.getStockItem(position).getLastTradePrice());
        percentChange.setText(portfolioManager.getStockItem(position).getChange());
        double opening;
        if(portfolioManager.getStockItem(position).getChange().substring(0,1).equalsIgnoreCase("+")){
            opening = Double.parseDouble(portfolioManager.getStockItem(position).getLastTradePrice()) - Double.parseDouble(portfolioManager.getStockItem(position).getChange().substring(1));
        }else{
            opening = Double.parseDouble(portfolioManager.getStockItem(position).getLastTradePrice()) + Double.parseDouble(portfolioManager.getStockItem(position).getChange().substring(1));
        }

        Log.d("Debug Info", String.valueOf(opening));
        openingPrice.setText(String.valueOf(opening));
        volume.setText(portfolioManager.getStockItem(position).getVolume());
    }


    private void setPosition(int i) {
        position = i;
    }

    private void setPortfolioManager(PortfolioManager portfolioManager) {
        this.portfolioManager = portfolioManager;
    }
}
