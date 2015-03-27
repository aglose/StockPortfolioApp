package com.andrew.androiddevelopment.stockportfolioapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Andrew on 3/24/2015.
 */
public class ListRecyclerHolder extends RecyclerView.ViewHolder {
    private TextView stockTitle;
    private TextView stockSymbol;
    private TextView stockPrice;
    private TextView stockChange;
    private TextView stockDayLow;
    private TextView stockDayHigh;

    public ListRecyclerHolder(View view) {
        super(view);
        stockTitle  = (TextView) view.findViewById(R.id.stockNameText);
        stockSymbol  = (TextView) view.findViewById(R.id.stockSymbolText);
        stockPrice  = (TextView) view.findViewById(R.id.stockPriceText);
        stockChange  = (TextView) view.findViewById(R.id.stockChangeText);
        stockDayLow  = (TextView) view.findViewById(R.id.stockDayLowText);
        stockDayHigh  = (TextView) view.findViewById(R.id.stockDayHighText);

    }


}
