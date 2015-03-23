package com.andrew.androiddevelopment.stockportfolioapp;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class MainNavigationScreen extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, StockCardFragment.OnFragmentInteractionListener {
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private static ArrayList<JSONObject> stocksForView = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation_screen);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    Handler showContent = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            JSONObject stockInfo = (JSONObject) msg.obj;
            StockCardFragment.newInstance(stockInfo);

            mNavigationDrawerFragment = (NavigationDrawerFragment)
                    getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

            boolean duplicate = checkForDuplicateStock(stockInfo);
            boolean badUserInput = checkForBadStock(stockInfo);

            if(duplicate){
                Toast.makeText(getApplicationContext(), "This stock already exists in your Portfolio", Toast.LENGTH_SHORT).show();
            }else if(badUserInput){
                Toast.makeText(getApplicationContext(), "The stock you entered does not exist", Toast.LENGTH_SHORT).show();
            }else{
                mNavigationDrawerFragment.addStockItem(stockInfo);
                stocksForView = mNavigationDrawerFragment.getStockItems();
                Toast.makeText(getApplicationContext(), "Stock Added to Portfolio", Toast.LENGTH_SHORT).show();
            }

            return false;
        }
    });

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

    private boolean checkForDuplicateStock(JSONObject stockInfo) {
        for (JSONObject stocks: stocksForView){
            try {
                if(stocks.get("Name").toString().equalsIgnoreCase(stockInfo.getString("Name"))){
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }


    public void onSectionAttached(int number) {
        if(stocksForView.size() == 0){
            mTitle = getString(R.string.welcomeTitle);
        }else{
            JSONObject stock = stocksForView.get(number-1);
            try {
                mTitle = stock.getString("Name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
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
    public void onFragmentInteraction(Uri uri) {

    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        ImageView imageView;
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_navigation_screen, container, false);
            imageView = (ImageView) rootView.findViewById(R.id.stockChartView);
            getStockChart();
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

        private void getStockChart(){
            if(stocksForView.size() > 0){
                Thread loadContent = new Thread(){
                    @Override
                    public void run(){
                        try {
                            JSONObject stockName = stocksForView.get(getArguments().getInt(ARG_SECTION_NUMBER)-1);
                            String stockURL = "https://chart.yahoo.com/z?t=NASDAQ%20&s="+stockName.get("symbol");

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
