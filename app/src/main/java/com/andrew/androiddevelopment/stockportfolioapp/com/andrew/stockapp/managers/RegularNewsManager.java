package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers;

import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.items.RegularNewsItem;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.items.StockNewsItem;

import java.util.ArrayList;

/**
 * Created by Andrew on 4/1/2015.
 */
public class RegularNewsManager {

    private ArrayList<RegularNewsItem> regularNewsItemArrayList = new ArrayList<>();

    public int getCount(){
        return regularNewsItemArrayList.size();
    }

    public void addStockNewsItem(RegularNewsItem newRegularNewsItem){
        regularNewsItemArrayList.add(newRegularNewsItem);
    }

    public RegularNewsItem getRegularNewsItem(int index){
        return regularNewsItemArrayList.get(index);
    }
}
