package com.andrew.androiddevelopment.stockportfolioapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.adapters.NewsCardAdapter;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.downloadertasks.ChartImageDownloaderTask;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.downloadertasks.StockNewsDownloaderTask;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.items.StockItem;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.listeners.OnSwipeTouchListener;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers.StockItemManager;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers.StockNewsManager;

import org.json.JSONException;
import org.json.JSONObject;


public class MainNavigationScreen extends ActionBarActivity implements NavigationCallbacks {
    private NavigationDrawerFragment mNavigationDrawerFragment;
    static Toolbar mToolbar;
    private static StockItemManager stockItemManager = new StockItemManager();
    private static StockNewsManager stockNewsManager = new StockNewsManager();
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
                .replace(R.id.container, MainScreenStockFragment.newInstance(position))
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

    public static class MainScreenStockFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        private RecyclerView newsListRecycler;
        private NewsCardAdapter newsCardAdapter;

        private ImageView imageView;

        private ChartImageDownloaderTask imageDownloader;
        private StockNewsDownloaderTask stockNewsDownloader;

        private static final String chartArray[] = new String[]{"1d","3d", "1w", "1m", "6m", "1y"};
        public static Drawable[] chartImages = new Drawable[8];
        int position = 0;


        public static MainScreenStockFragment newInstance(int sectionNumber) {
            MainScreenStockFragment fragment = new MainScreenStockFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;
        }

        public MainScreenStockFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_navigation_screen, container, false);

            imageView = (ImageView) rootView.findViewById(R.id.stockChartView);
            applyTouchListener(imageView);

            newsListRecycler = (RecyclerView) rootView.findViewById(R.id.newsList);
            newsCardAdapter = new NewsCardAdapter(getActivity(), stockNewsManager);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            newsListRecycler.setAdapter(newsCardAdapter);
            newsListRecycler.setHasFixedSize(true);
            newsListRecycler.setLayoutManager(layoutManager);

            int position = getArguments().getInt(ARG_SECTION_NUMBER);
            if(position >= 0){
                while(position >= stockItemManager.getCount()){
                    position--;
                }
                getStockChart(position);
                getStockNews(position, rootView);
            }
            return rootView;
        }

        private void applyTouchListener(final ImageView imageView) {
            imageView.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
                @Override
                public void onSwipeDown() {
                }
                @Override
                public void onSwipeLeft() {
                    if(position < 6){
                        imageView.setImageDrawable(chartImages[position++]);
                    }
                    if(position > 5){
                        position = 6;
                    }
                    Toast.makeText(getActivity(), "Left "+String.valueOf(position), Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onSwipeUp() {
                }
                @Override
                public void onSwipeRight() {
                    if(position >= 0){
                        imageView.setImageDrawable(chartImages[position--]);
                    }
                    if(position < 0){
                        position = 0;
                    }
                    Toast.makeText(getActivity(), "Right "+String.valueOf(position), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void getStockChart(final int position){
            if(stockItemManager.getCount() > 0 ) {
                StockItem stockSymbol = stockItemManager.getStockItem(position);
                String urls[] = new String[6];
                for (int i = 0; i < 6; i++) {
                    String stockURL = "https://chart.yahoo.com/z?t=" + chartArray[i] + "%20&s=" + stockSymbol.getSymbol();
                    urls[i] = stockURL;
                }
                if(((MainNavigationScreen)getActivity()).isNetworkActive()){
                    imageDownloader = new ChartImageDownloaderTask(chartImages, new ChartImageDownloaderTask.ImageLoaderListener() {
                        @Override
                        public void onImagesDownloaded(Drawable[] chartImagesUpdated) {
                            chartImages = chartImagesUpdated;
                            imageView.setImageDrawable(chartImages[0]);
                        }
                    }, imageView);
                    imageDownloader.execute(urls);
                }
            }
        }

        private void getStockNews(final int position, final View rootView){

            if(stockItemManager.getCount() > 0 ) {
                StockItem stockSymbol = stockItemManager.getStockItem(position);
                final String symbol = stockSymbol.getSymbol();
                String stockNewsURL = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20google.news%20where%20q%20%3D%20%22"+symbol+"%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

                if(((MainNavigationScreen)getActivity()).isNetworkActive()){
                    stockNewsDownloader = new StockNewsDownloaderTask(getActivity(), rootView, symbol);
                    stockNewsDownloader.execute(stockNewsURL);
                }
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
