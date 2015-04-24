package com.andrew.androiddevelopment.stockportfolioapp;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.items.PortfolioStockItem;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers.PortfolioManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class StockPollingService extends BroadcastReceiver {
    PortfolioManager portfolioManager = new PortfolioManager();
    String fullQuery = "";
    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent("STOCKS_UPDATED"));
    }
}
