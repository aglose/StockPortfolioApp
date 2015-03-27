package com.andrew.androiddevelopment.stockportfolioapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class MainNavigationScreen extends ActionBarActivity implements NavigationCallbacks {
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;
    private static StockItemManager stockItemManager = new StockItemManager();;
    private static ArrayList<PlaceholderFragment> fragments = new ArrayList<>();
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

    Handler createNewStock = new Handler(new Handler.Callback() {
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
            menuTitle.setText(stock.getName());
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
//        ArrayList<JSONObject> stocksList = gson.fromJson(stocksJson, new TypeToken<ArrayList<JSONObject>>(){}.getType());
//
//        if(stocksList != null){
//            if(stocksList.size() != 0){
//                Log.d("Debug onStart", stocksList.get(0).getClass().getName());
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
//        SharedPreferences appSharedPrefs = PreferenceManager
//                .getDefaultSharedPreferences(getApplicationContext());
//        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(stocksForView, new TypeToken<ArrayList<JSONObject>>(){}.getType());
//        prefsEditor.putString("SavedStocks", json);
//        prefsEditor.commit();
//        if(stocksForView.size() != 0) {
//            Log.d("Debug onStop", stocksForView.get(0).toString());
//        }
    }

    @Override
    public void onItemSelected(int position) {

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position))
                .commit();
        //I CHANGED THE POSITION FROM POSITION + 1
    }

    public StockItemManager getStockItemManager() {
        return stockItemManager;
    }
    public void setStockItemManager(StockItemManager stockItemManager) {
        this.stockItemManager = stockItemManager;
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        ImageView imageView;


        public static void refreshFragments(){
            for(PlaceholderFragment frags: fragments){
                int currentSection = frags.getArguments().getInt(ARG_SECTION_NUMBER);
                Bundle newArgs = new Bundle();
                newArgs.putInt(ARG_SECTION_NUMBER, --currentSection);
                frags.setArguments(newArgs);
            }
        }
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            if(sectionNumber >= 0){
                fragments.add(fragment);
            }

            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_navigation_screen, container, false);
            imageView = (ImageView) rootView.findViewById(R.id.stockChartView);
            int position = getArguments().getInt(ARG_SECTION_NUMBER);
            Log.d("Debug stockChart ", String.valueOf(getArguments().getInt(ARG_SECTION_NUMBER)));
            if(position >= 0){
                while(position >= stockItemManager.getCount()){
                    position--;
                }
                getStockChart(position);
            }
            return rootView;
        }

        Handler showChart = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Drawable stockChart = (Drawable) msg.obj;
                imageView.setImageDrawable(stockChart);
                return false;
            }
        });

        private void getStockChart(final int position){
            if(stockItemManager.getCount() > 0 ){
                Thread loadContent = new Thread(){
                    @Override
                    public void run(){
                        try {
                            StockItem stockSymbol = stockItemManager.getStockItem(position);
                            String stockURL = "https://chart.yahoo.com/z?t=NASDAQ%20&s="+stockSymbol.getSymbol();

                            InputStream stream = (InputStream) new URL(stockURL).getContent();
                            Drawable chart = Drawable.createFromStream(stream, "chart pic");

                            Message msg = Message.obtain();
                            msg.obj = chart;
                            showChart.sendMessage(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                loadContent.start();
            }

        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainNavigationScreen) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}
