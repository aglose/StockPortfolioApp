package com.andrew.androiddevelopment.stockportfolioapp;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.fragments.ChartTabFragment;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.fragments.RegularNewsFragment;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.fragments.StockNewsDisplayFragment;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.fragments.NavigationDrawerFragment;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.items.PortfolioStockItem;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers.PortfolioManager;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers.StockNewsManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import it.gmariotti.cardslib.library.view.CardView;
import it.gmariotti.cardslib.library.view.CardViewNative;

public class MainNavigationScreen extends ActionBarActivity implements NavigationCallbacks {
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private RegularNewsFragment regularNewsFragment;

    private Toolbar mToolbar;
    private PortfolioManager portfolioManager = new PortfolioManager();
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


        regularNewsFragment = (RegularNewsFragment) getSupportFragmentManager().findFragmentById(R.id.initial_news);

        regularNewsFragment = new RegularNewsFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.initial_news, regularNewsFragment.newInstance(true), "RegularNewsFragment").commit();

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

        boolean duplicate = portfolioManager.checkForDuplicateStock(stockInfo);
        boolean badUserInput = checkForBadStock(stockInfo);

        if(!duplicate){
            portfolioManager.addStockItem(stockInfo);
            Toast.makeText(getApplicationContext(), "Stock Added to Portfolio", Toast.LENGTH_SHORT).show();
        }
        if(badUserInput){
            Toast.makeText(getApplicationContext(), "The stock you entered does not exist", Toast.LENGTH_SHORT).show();
        }else{
            mNavigationDrawerFragment.notifyAdapterOfNewStock();
        }
        for(PortfolioStockItem stock: portfolioManager.getStockList()){
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
        while(number >= portfolioManager.getCount()){
            number--;
        }
        if(portfolioManager.getCount() == 0 || number == -1){
            menuTitle.setText(R.string.app_name);
        }else{
            Log.d("Debug number ", String.valueOf(number));
            PortfolioStockItem stock = portfolioManager.getStockItem(number);
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
        int id = item.getItemId();

        if (id == R.id.home) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
//
            RegularNewsFragment mainScreenFragment = new RegularNewsFragment();
            Fragment chartFragment = fragmentManager.findFragmentByTag("ChartFragment");
            Fragment newDisplayFragment = fragmentManager.findFragmentByTag("StockNewsFragment");

            if(chartFragment != null){
                Log.d("Debug fragment",chartFragment.toString());
                Log.d("Debug fragment",newDisplayFragment.toString());
                transaction.remove(chartFragment);
                transaction.remove(newDisplayFragment);
            }

            menuTitle.setText(R.string.regularNewsTitle);
            transaction.replace(R.id.initial_news, mainScreenFragment.newInstance(false), "RegularNewsFragment");
            transaction.commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart(){
        super.onStart();
        checkForSavedStocks();
    }

    private boolean checkForSavedStocks() {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        prefsEditor.commit();
        String stocksJson = appSharedPrefs.getString("SavedStocks", "");
        if(stocksJson.length() > 0){
            Log.d("Debug onStart", stocksJson);
            ArrayList<PortfolioStockItem> stockItemList = gson.fromJson(stocksJson, new TypeToken<ArrayList<PortfolioStockItem>>(){}.getType());
            portfolioManager.setStockItemList(stockItemList);
            return true;
        }
        return false;
    }

    @Override
    public void onStop(){
        super.onStop();
        if(portfolioManager.getCount() != 0){
            saveStocksToPreferences();
        }
    }

    public void saveStocksToPreferences(){
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(portfolioManager.getStockList(), new TypeToken<ArrayList<PortfolioStockItem>>(){}.getType());
        prefsEditor.putString("SavedStocks", json);
        prefsEditor.commit();
        if(portfolioManager.getStockList().size() != 0) {
            Log.d("Debug onStop", portfolioManager.getStockList().get(0).toString());
        }
    }

    @Override
    public void onItemSelected(int position) {

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment regularNewsFragment = getSupportFragmentManager().findFragmentByTag("RegularNewsFragment");
        Fragment stockNewsFragment = getSupportFragmentManager().findFragmentByTag("StockNewsFragment");
        Fragment chartFragment = getSupportFragmentManager().findFragmentByTag("StockNewsFragment");

        if(regularNewsFragment != null){
            transaction.detach(regularNewsFragment);
        }
        if(stockNewsFragment != null){
            transaction.detach(stockNewsFragment);
        }
        if(chartFragment != null){
            transaction.detach(chartFragment);
        }

        // Get Chart Fragment with Tabs
        ChartTabFragment fragment = new ChartTabFragment(portfolioManager, position);
        transaction.replace(R.id.chart_content_fragment, fragment, "ChartFragment");

        //Get Stock News Fragment
        StockNewsDisplayFragment newDisplayFragment = new StockNewsDisplayFragment();
        transaction
                .replace(R.id.container, newDisplayFragment.newInstance(position, getStockNewsManager(), getPortfolioManager()), "StockNewsFragment")
                .commit();
    }

    public PortfolioManager getPortfolioManager() {
        if(portfolioManager == null){
            portfolioManager = new PortfolioManager();
        }
        return portfolioManager;
    }
    public void setPortfolioManager(PortfolioManager portfolioManager) {
        this.portfolioManager = portfolioManager;
    }

    public StockNewsManager getStockNewsManager() {
        return stockNewsManager;
    }
    public void setStockNewsManager(StockNewsManager stockNewsManager) {
        this.stockNewsManager = stockNewsManager;
    }

    public void onToggleClicked(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        StockNewsDisplayFragment newDisplayFragment = (StockNewsDisplayFragment) fragmentManager.findFragmentByTag("StockNewsFragment");

        boolean on = ((Switch) view).isChecked();

        if (on) {
            newDisplayFragment.changeStockNews(true);
        } else {
            newDisplayFragment.changeStockNews(false);
        }
    }
}
