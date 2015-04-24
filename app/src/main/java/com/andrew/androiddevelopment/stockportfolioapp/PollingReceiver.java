package com.andrew.androiddevelopment.stockportfolioapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.andrew.androiddevelopment.stockportfolioapp.StockPollingService;

/**
 * Created by Andrew on 4/22/2015.
 */
public class PollingReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, StockPollingService.class);
        context.startService(service);
    }
}
