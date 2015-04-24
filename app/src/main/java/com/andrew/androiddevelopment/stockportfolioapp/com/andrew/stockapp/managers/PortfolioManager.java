package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers;

import android.util.Log;

import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.items.PortfolioStockItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Andrew on 3/26/2015.
 */
public class PortfolioManager {


    private ArrayList<PortfolioStockItem> stockList = new ArrayList<>();

    public PortfolioManager(){
    }

    public int getCount(){
        if(stockList != null){
            return stockList.size();
        }else{
            return 0;
        }
    }

    public void addStockItem(JSONObject newStock){
        PortfolioStockItem newItem = createNewStockFromJSON(newStock);
        stockList.add(newItem);
    }

    private PortfolioStockItem createNewStockFromJSON(JSONObject newStock){
        PortfolioStockItem newPortfolioStockItem = new PortfolioStockItem();
        try {
            newPortfolioStockItem.setLastTradePrice(newStock.get("LastTradePriceOnly").toString());
            newPortfolioStockItem.setName(newStock.get("Name").toString());
            newPortfolioStockItem.setSymbol(newStock.get("Symbol").toString().toUpperCase());
            newPortfolioStockItem.setChange(newStock.get("Change").toString());
            newPortfolioStockItem.setDaysLow(newStock.get("DaysLow").toString());
            newPortfolioStockItem.setDaysHigh(newStock.get("DaysHigh").toString());
            newPortfolioStockItem.setVolume(newStock.get("Volume").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newPortfolioStockItem;
    }

    public String getAllStockSymbols(){
        String fullQuery = "";
        for(PortfolioStockItem stock: stockList){
            String stockSymbol = stock.getSymbol();
            if(stockSymbol != null){
                fullQuery += "%2C%22"+stockSymbol+"%22";
            }
        }
        return fullQuery;
    }

    public boolean checkForDuplicateStock(JSONObject stockInfo) {
        PortfolioStockItem updatedStock = createNewStockFromJSON(stockInfo);
        for (int i=0;i<stockList.size();i++){
            if(stockList.get(i).getName().toString().equalsIgnoreCase(updatedStock.getName())){
                Log.d("Debug ", "refreshed "+stockList.get(i).getName());
                stockList.set(i, updatedStock);
                return true;
            }
        }
        return false;
    }

    public PortfolioStockItem getStockItem(int index){
        return stockList.get(index);
    }

    public void setStockItem(int index, PortfolioStockItem portfolioStockItem){
        stockList.set(index, portfolioStockItem);
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
            PortfolioStockItem fromStock = getStockItem(fromPosition);
            PortfolioStockItem swapStock = getStockItem(fromPosition+direction);
            stockList.set(fromPosition+(i*direction), fromStock);
            stockList.set(fromPosition, swapStock);
        }

        for(int i=0; i < stockList.size(); i++){
            Log.d("Debug StockList after move", stockList.get(i).getName());
        }
    }

    public ArrayList<PortfolioStockItem> getStockList(){
        return stockList;
    }
    public int undoLastRemoval(){
        return 0;
    }

    public void setStockItemList(ArrayList<PortfolioStockItem> portfolioStockItemList) {
        this.stockList = portfolioStockItemList;
    }
}
