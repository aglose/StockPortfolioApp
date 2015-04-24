package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.fragments;

import android.content.Context;
import android.os.Message;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.view.DividerItemDecoration;
import com.andrew.androiddevelopment.stockportfolioapp.MainNavigationScreen;
import com.andrew.androiddevelopment.stockportfolioapp.NavigationCallbacks;
import com.andrew.androiddevelopment.stockportfolioapp.R;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers.PortfolioManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment implements NavigationCallbacks {
    private NavigationCallbacks mCallbacks;
    private RecyclerView mDrawerRecyclerView;
    private View mFragmentContainerView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private PortfolioListFragment.RecyclerAdapter adapter;
    private AutoCompleteTextView searchText;
    private Button stockButton;
    private NavigationDrawerFragment navigationDrawerFragment;
    private int selectedPosition = 0;
    private int mCurrentSelectedPosition;
    private boolean[] itemCheckedArray = new boolean[30];
    private boolean mUserLearnedDrawer;
    private View rootView;
    public NavigationDrawerFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        navigationDrawerFragment = this;
        rootView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mDrawerRecyclerView = (RecyclerView) rootView.findViewById(R.id.drawerList);
        mDrawerRecyclerView.setHasFixedSize(true);
        mDrawerRecyclerView.setLayoutManager(layoutManager);

        adapter = new PortfolioListFragment.RecyclerAdapter(getActivity(), getStockItemManager());
//        wrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(adapter);
        adapter.setNavigationCallbacks(this);
        mDrawerRecyclerView.setAdapter(adapter);
        mDrawerRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mDrawerRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        selectRow(mCurrentSelectedPosition);

        setUpBatchDeleteButton();
        return rootView;
    }

    private void setUpBatchDeleteButton() {
        final Button deleteButton = (Button) rootView.findViewById(R.id.batchDeleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = itemCheckedArray.length-1; i >= 0; i--){
                    if(itemCheckedArray[i] == true){
                        itemCheckedArray[i] = false;
                        notifyAdapterOfRemovedStock(i);
                    }
                }
                PortfolioListFragment.RecyclerAdapter.unCheckAll = true;
                adapter = new PortfolioListFragment.RecyclerAdapter(getActivity(), getStockItemManager());
                adapter.setNavigationCallbacks(navigationDrawerFragment);
                mDrawerRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                deleteButton.setVisibility(View.INVISIBLE);
                PortfolioListFragment.RecyclerAdapter.unCheckAll = false;

            }
        });
    }

    public void notifyAdapterOfNewStock(){
        int index = getStockItemManager().getCount();
        adapter.addItem(index);
    }
    public void notifyAdapterOfRemovedStock(int position){
        adapter.removeItem(position);
    }

    private String[] stockSymbols(){
        BufferedReader reader;
        String stockSym = "";
        ArrayList<String> stockSymbols = new ArrayList<>();
        String stockArray[] = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getActivity().getAssets().open("stockSymbols.txt")));

            while((stockSym = reader.readLine()) != null){
                stockSymbols.add(stockSym);
            }
            stockArray =  stockSymbols.toArray(new String[stockSymbols.size()]);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return stockArray;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_search) {
            getStockInfo(item);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getStockInfo(MenuItem item){
        stockButton = (Button) item.getActionView().findViewById(R.id.getStockButton);
        final String[] autoFill = stockSymbols();
        ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(),
                R.layout.list_layout, autoFill);

        searchText = (AutoCompleteTextView) item.getActionView().findViewById(R.id.autoCompleteTextView);
        searchText.requestFocus();
        searchText.setAdapter(adapter);

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        stockButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
                if(searchText.getText().toString().length() != 0){
                    pollForStocksThread(searchText.getText().toString());
                    searchText.setText("");
                }
            }
        });


    }
    public void pollForStocksThread(String newItem){
        final String newStock = newItem.trim();
        Thread loadContent = new Thread(){
            @Override
            public void run(){
                if (((MainNavigationScreen)getActivity()).isNetworkActive()){
                    InputStream is = null;
                    String result = "";
                    String fullQuery = "%22"+newStock+"%22";


                    JSONObject jArray = null;

                    try{
                        if(getStockItemManager().getCount() > 1){
                            fullQuery += getStockItemManager().getAllStockSymbols();
                        }
                        String url = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quote%20where%20symbol%20in%20("+fullQuery+")&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost httppost = new HttpPost(url);
                        HttpResponse response = httpclient.execute(httppost);
                        HttpEntity entity = response.getEntity();
                        is = entity.getContent();

                    }catch(Exception e){
                        Log.e("log_tag", "Error in http connection " + e.toString());
                    }

                    try{
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        is.close();
                        result=sb.toString();
                    }catch(Exception e){
                        Log.e("log_tag", "Error converting result "+e.toString());
                    }

                    try{
                        jArray = new JSONObject(result);
                        JSONObject results = jArray.getJSONObject("query").getJSONObject("results");

                        int i = 0;
                        if(results.get("quote") instanceof JSONArray){
                            JSONArray quotesArray = results.getJSONArray("quote");
                            while(quotesArray.length() != i){
                                Message msg = Message.obtain();
                                msg.obj = quotesArray.getJSONObject(i);
                                ((MainNavigationScreen)getActivity()).createNewStock.sendMessage(msg);
                                i++;
                            }
                        }else{
                            Message msg = Message.obtain();
                            msg.obj = results.get("quote");
                            ((MainNavigationScreen)getActivity()).createNewStock.sendMessage(msg);
                        }
                    }catch(JSONException e){
                        Log.e("log_tag", "Error parsing data "+e.toString());
                    }
                }
            }
        };
        loadContent.start();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    public void initDrawer(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setStatusBarBackgroundColor(
                getResources().getColor(R.color.myPrimaryDarkColor));

        mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) return;

                getActivity().invalidateOptionsMenu();

                if (mCallbacks != null) {
                    mCallbacks.onItemSelected(selectedPosition);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) return;
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                }

                getActivity().invalidateOptionsMenu();
            }
        };

        /* Used for drawer icon animation */
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mActionBarDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mFragmentContainerView);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }


    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        mDrawerLayout = drawerLayout;
    }

    void selectRow(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }

        selectedPosition = position;
        if (mCallbacks != null) {
            mCallbacks.onItemSelected(position);
        }
        ((PortfolioListFragment.RecyclerAdapter) mDrawerRecyclerView.getAdapter()).setSelectedRow(position);
    }

    @Override
    public void onItemSelected(int position) {

    }

    public PortfolioManager getStockItemManager() {
        return ((MainNavigationScreen) getActivity()).getPortfolioManager();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }

    @Override
    public void itemChecked(int i) {
        itemCheckedArray[i] = true;
        Button deleteButton = (Button) rootView.findViewById(R.id.batchDeleteButton);

        if(deleteButton.getVisibility() != View.VISIBLE){
            deleteButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void itemUnChecked(int i) {
        itemCheckedArray[i] = false;
        boolean keepButton = false;
        Button deleteButton = (Button) rootView.findViewById(R.id.batchDeleteButton);

        for (int j = 0; j<itemCheckedArray.length; j++){
            if(itemCheckedArray[j] == true){
                keepButton = true;
                break;
            }
        }
        if(!keepButton){
            deleteButton.setVisibility(View.INVISIBLE);
        }
    }
}
