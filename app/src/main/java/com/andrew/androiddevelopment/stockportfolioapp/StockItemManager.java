package com.andrew.androiddevelopment.stockportfolioapp;

import android.graphics.Color;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Andrew on 3/26/2015.
 */
public class StockItemManager {
    private ArrayList<StockItem> stockList;

    public StockItemManager(){
        stockList = new ArrayList<>();
    }

    public int getCount(){
        return stockList.size();
    }

    public void addStockItem(JSONObject newStock){
        StockItem newItem = createNewStockFromJSON(newStock);
        stockList.add(newItem);
    }

    private StockItem createNewStockFromJSON(JSONObject newStock){
        StockItem newStockItem = new StockItem();
        try {
            newStockItem.setLastTradePrice(newStock.get("LastTradePriceOnly").toString());
            newStockItem.setName(newStock.get("Name").toString());
            newStockItem.setSymbol(newStock.get("Symbol").toString().toUpperCase());
            newStockItem.setChange(newStock.get("Change").toString());
            newStockItem.setDaysLow(newStock.get("DaysLow").toString());
            newStockItem.setDaysHigh(newStock.get("DaysHigh").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newStockItem;
    }

    public String getAllStockSymbols(){
        String fullQuery = "";
        for(StockItem stock: stockList){
            String stockSymbol = (String) stock.getSymbol();
            if(stockSymbol != null){
                fullQuery += "%2C%22"+stockSymbol+"%22";
            }
        }
        return fullQuery;
    }

    public boolean checkForDuplicateStock(JSONObject stockInfo) {
        StockItem updatedStock = createNewStockFromJSON(stockInfo);
        for (int i=0;i<stockList.size();i++){
            if(stockList.get(i).getName().toString().equalsIgnoreCase(updatedStock.getName())){
                Log.d("Debug ", "refreshed "+stockList.get(i).getName());
                stockList.set(i, updatedStock);
                return true;
            }
        }
        return false;
    }

    public StockItem getStockItem(int index){
        return stockList.get(index);
    }

    public void setStockItem(int index, StockItem stockItem){
        stockList.set(index, stockItem);
    }

    public void removeStock(int position){
        stockList.remove(position);
    }

    public void moveItem(int fromPosition, int toPosition){
        int distance = fromPosition - toPosition;
        int direction;

        if(distance > 0 ){
            direction = 1;
        }else{
            direction = -1;
        }

        for (int i=1; i <= Math.abs(distance);i++){
            StockItem fromStock = getStockItem(fromPosition);
            StockItem swapStock = getStockItem(fromPosition+direction);
            stockList.set(fromPosition+(i*direction), fromStock);
            stockList.set(fromPosition, swapStock);
        }

        for(int i=0; i < stockList.size(); i++){
            Log.d("Debug StockList after move", stockList.get(i).getName());
        }
    }

    public ArrayList<StockItem> getStockList(){
        return stockList;
    }
    public int undoLastRemoval(){
        return 0;
    }
}
