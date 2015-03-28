package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.items;

/**
 * Created by Andrew on 3/26/2015.
 */
public class StockItem {
    private String name = "";
    private String symbol = "";
    private String change = "";
    private String daysHigh = "";
    private String daysLow = "";
    private String price = "";
    private String lastTradePriceOnly = "";
    private String fullName = "";

    public long getId(){
        return 0;
    }

    public boolean isSectionHeader(){
        return false;
    }

    public int getViewType(){
        return 0;
    }

    public int getSwipeReactionType(){
        return 0;
    }

    public String getText(){
        return null;
    }

    public void setPinnedToSwipeLeft(boolean pinned){

    }

    public boolean isPinnedToSwipeLeft(){
        return false;
    }

    public void setLastTradePrice(String lastTradePriceOnly) {
        this.lastTradePriceOnly = lastTradePriceOnly;
    }

    public String getFullName(){
        return fullName;
    }
    public void setName(String name) {
        this.fullName = name;
        String localName = name;
        if(name.length() > 10){
            StringBuilder myName = new StringBuilder(name);
            String newString = myName.substring(0, 11);
            StringBuilder finalString = new StringBuilder(newString);
            finalString.append("...");
            localName = finalString.toString();
        }
        this.name = localName;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public void setDaysHigh(String daysHigh) {
        this.daysHigh = daysHigh;
    }

    public void setDaysLow(String daysLow) {
        this.daysLow = daysLow;
    }

    public String getLastTradePrice() {
        return lastTradePriceOnly;
    }

    public String getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getChange() {
        return change;
    }

    public String getDaysHigh() {
        return daysHigh;
    }

    public String getDaysLow() {
        return daysLow;
    }

}
