package com.andrew.androiddevelopment.stockportfolioapp;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.fragments.ChartTabFragment;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.fragments.MainStockNewsDisplayFragment;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.fragments.NavigationDrawerFragment;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.items.StockItem;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers.StockItemManager;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers.StockNewsManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainNavigationScreen extends ActionBarActivity implements NavigationCallbacks {
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;
    private StockItemManager stockItemManager = new StockItemManager();
    private StockNewsManager stockNewsManager = new StockNewsManager();
    private TextView menuTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation_screen);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.initDrawer(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);

        onItemSelected(0);
        menuTitle = (TextView) mToolbar.findViewById(R.id.menuTitle);
        menuTitle.setText(R.string.app_name);


    }

    public Handler createNewStock = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            JSONObject stockInfo = (JSONObject) msg.obj;
            finishingCreatingStock(stockInfo);
            return false;
        }
    });

    private void finishingCreatingStock(JSONObject stockInfo) {
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);


        boolean duplicate = stockItemManager.checkForDuplicateStock(stockInfo);
        boolean badUserInput = checkForBadStock(stockInfo);

        if(!duplicate){
            stockItemManager.addStockItem(stockInfo);
            Toast.makeText(getApplicationContext(), "Stock Added to Portfolio", Toast.LENGTH_SHORT).show();
        }
        if(badUserInput){
            Toast.makeText(getApplicationContext(), "The stock you entered does not exist", Toast.LENGTH_SHORT).show();
        }else{
            mNavigationDrawerFragment.notifyAdapterOfNewStock();
        }
        for(StockItem stock: stockItemManager.getStockList()){
            Log.d("Debug StockList after move ", stock.getName());
        }
    }

    private boolean checkForBadStock(JSONObject stockInfo) {
        try {
            if(stockInfo.getString("Name").equalsIgnoreCase("null")){
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isNetworkActive(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            Toast.makeText(this, "No Network Access", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void onSectionAttached(int number) {
        while(number >= stockItemManager.getCount()){
            number--;
        }
        if(stockItemManager.getCount() == 0 || number == -1){
            menuTitle.setText(R.string.app_name);
        }else{
            Log.d("Debug number ", String.valueOf(number));
            StockItem stock = stockItemManager.getStockItem(number);
            menuTitle.setText(stock.getFullName());
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main_navigation_screen, menu);
            restoreActionBar();
            return true;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            FragmentManager fragmentManager = getFragmentManager();

            fragmentManager.beginTransaction()
                    .replace(R.id.container, null, "delete")
                    .commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart(){
        super.onStart();
//        SharedPreferences appSharedPrefs = PreferenceManager
//                .getDefaultSharedPreferences(getApplicationContext());
//        Gson gson = new Gson();
//        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
//        prefsEditor.remove("SavedStocks");
//        prefsEditor.commit();
//        String stocksJson = appSharedPrefs.getString("SavedStocks", "");
//        ArrayList<StockItem> stockItemList = gson.fromJson(stocksJson, new TypeToken<ArrayList<StockItem>>(){}.getType());
//        stockItemManager.setStockItemList(stockItemList);
//
//        if(stockItemList != null){
//            if(stockItemList.size() != 0){
//                Log.d("Debug onStart", stockItemList.get(0).getClass().getName());
//            }
//        }
    }

    @Override
    public void onStop(){
        super.onStop();
        saveStocksToPreferences();
    }

    //TODO
    public void saveStocksToPreferences(){
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(stockItemManager.getStockList(), new TypeToken<ArrayList<StockItem>>(){}.getType());
        prefsEditor.putString("SavedStocks", json);
        prefsEditor.commit();
        if(stockItemManager.getStockList().size() != 0) {
            Log.d("Debug onStop", stockItemManager.getStockList().get(0).toString());
        }
    }

    @Override
    public void onItemSelected(int position) {

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // Get Chart Fragment with Tabs
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ChartTabFragment fragment = new ChartTabFragment(stockItemManager, position);
        transaction.replace(R.id.chart_content_fragment, fragment);
        transaction.commit();

        //Get Stock News Fragment
        FragmentManager fragmentManager = getFragmentManager();
        MainStockNewsDisplayFragment newDisplayFragment = new MainStockNewsDisplayFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.container, newDisplayFragment.newInstance(position, getStockNewsManager(), getStockItemManager()), "Fragment")
                .commit();
        //I CHANGED THE POSITION FROM POSITION + 1
    }

    public StockItemManager getStockItemManager() {
        return stockItemManager;
    }
    public void setStockItemManager(StockItemManager stockItemManager) {
        this.stockItemManager = stockItemManager;
    }

    public StockNewsManager getStockNewsManager() {
        return stockNewsManager;
    }
    public void setStockNewsManager(StockNewsManager stockNewsManager) {
        this.stockNewsManager = stockNewsManager;
    }

    public void onToggleClicked(View view) {
        FragmentManager fragmentManager = getFragmentManager();

        MainStockNewsDisplayFragment newDisplayFragment = (MainStockNewsDisplayFragment) fragmentManager.findFragmentByTag("Fragment");

        boolean on = ((Switch) view).isChecked();

        if (on) {
            newDisplayFragment.changeStockNews(true);
        } else {
            newDisplayFragment.changeStockNews(false);
        }
    }
}
