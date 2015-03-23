package com.andrew.androiddevelopment.stockportfolioapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Andrew on 3/18/2015.
 */
public class StockCardAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<JSONObject> stockList;
    private TextView stockTitle;
    private TextView stockSymbol;
    private TextView stockPrice;
    private TextView stockChange;
    private TextView stockDayLow;
    private TextView stockDayHigh;

    public StockCardAdapter(Context c, ArrayList stockList) {
        this.stockList = stockList;
        context = c;
    }

    @Override
    public int getCount() {
        return stockList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View list;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            list = inflater.inflate(R.layout.fragment_stock_card, null);
            stockTitle  = (TextView) list.findViewById(R.id.stockTitleText);
            stockSymbol  = (TextView) list.findViewById(R.id.stockSymbolText);
            stockPrice  = (TextView) list.findViewById(R.id.stockPriceText);
            stockChange  = (TextView) list.findViewById(R.id.stockChangeText);
            stockDayLow  = (TextView) list.findViewById(R.id.stockDayLowText);
            stockDayHigh  = (TextView) list.findViewById(R.id.stockDayHighText);

            JSONObject stock = stockList.get(position);

            try {
                if(stock.get("Change").toString().startsWith("+")){
                    stockPrice.setText(stock.get("LastTradePriceOnly").toString());
                    stockPrice.setTextColor(Color.GREEN);
                }else{
                    stockPrice.setText(stock.get("LastTradePriceOnly").toString());
                    stockPrice.setTextColor(Color.RED);
                }
                stockTitle.setText(stock.get("Name").toString());
                stockSymbol.setText(stock.get("Symbol").toString().toUpperCase());

                stockChange.setText(stock.get("Change").toString());
                stockDayLow.setText(stock.get("DaysLow").toString());
                stockDayHigh.setText(stock.get("DaysHigh").toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            list = (View) convertView;
        }
        return list;
    }




}
