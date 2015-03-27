package com.andrew.androiddevelopment.stockportfolioapp;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Andrew on 3/24/2015.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ListRecyclerHolder>{
    private final Context context;
    private int mSelectedPosition;
    private int mTouchedPosition = -1;
    private NavigationCallbacks mNavigationCallbacks;
    private static final String TAG = "MyDraggableItemAdapter";
    private StockItemManager stockItemManager;

    public RecyclerAdapter(Context context, StockItemManager stockItemManager) {
        this.context = context;
        this.stockItemManager = stockItemManager;
    }

    public void setNavigationCallbacks(NavigationCallbacks navigationCallbacks) {
        mNavigationCallbacks = navigationCallbacks;
    }

    @Override
    public RecyclerAdapter.ListRecyclerHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.stock_card_layout, viewGroup, false);
        return new ListRecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ListRecyclerHolder viewHolder, final int i) {
            viewHolder.stockName.setText(String.valueOf(stockItemManager.getStockItem(i).getName()));
            if (stockItemManager.getStockItem(i).getChange().toString().startsWith("+")) {
                viewHolder.stockPrice.setText(String.valueOf(stockItemManager.getStockItem(i).getLastTradePrice()));
                viewHolder.stockPrice.setTextColor(Color.GREEN);
            } else {
                viewHolder.stockPrice.setText(String.valueOf(stockItemManager.getStockItem(i).getLastTradePrice()));
                viewHolder.stockPrice.setTextColor(Color.RED);
            }
            viewHolder.stockSymbol.setText(String.valueOf(stockItemManager.getStockItem(i).getSymbol()));
            viewHolder.stockChange.setText(String.valueOf(stockItemManager.getStockItem(i).getChange()));
            viewHolder.stockDayLow.setText(String.valueOf(stockItemManager.getStockItem(i).getDaysLow()));
            viewHolder.stockDayHigh.setText(String.valueOf(stockItemManager.getStockItem(i).getDaysHigh()));

            /* handle gestures and click events */
            handleRowEvents(viewHolder.itemView, i);
            handleDeleteBar(viewHolder.deleteBar, i);

            /* Highlight selected row */
            if (mSelectedPosition == i || mTouchedPosition == i) {
                viewHolder.itemView.setBackgroundColor(Color.parseColor("#BFD596"));
            } else {
                viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }
    }

    private void handleDeleteBar(final View deleteView, final int i){
        deleteView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                ((MainNavigationScreen) context).onNavigationDrawerItemSelected(-1);
                android.support.v4.app.FragmentManager fragmentManager = ((MainNavigationScreen) context).getSupportFragmentManager();
                NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) fragmentManager.findFragmentById(R.id.navigation_drawer);
                navigationDrawerFragment.notifyAdapterOfRemovedStock(i);
//                MainNavigationScreen.PlaceholderFragment.refreshFragments();
                return false;
            }
        });
    }
    private void handleRowEvents(final View itemView, final int i) {

//        itemView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//
//                    switch (event.getAction()) {
//                        case MotionEvent.ACTION_DOWN:
//                            touchPosition(i);
//                            return false;
//                        case MotionEvent.ACTION_UP:
//                        case MotionEvent.ACTION_CANCEL:
//                            touchPosition(-1);
//                            return false;
//                        case MotionEvent.ACTION_MOVE:
//                            return false;
//                    }
//                    return true;
//                }
//            }
//        );
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Debug", "short press");
                android.support.v4.app.FragmentManager fragmentManager = ((MainNavigationScreen) context).getSupportFragmentManager();
                NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) fragmentManager.findFragmentById(R.id.navigation_drawer);
                navigationDrawerFragment.selectRow(i);
                ((MainNavigationScreen) context).onNavigationDrawerItemSelected(i);
                ((MainNavigationScreen) context).saveStocksToPreferences();
            }
        });
    }

    public void addItem(int position) {
        notifyItemInserted(position);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        Log.d("Debug remove", String.valueOf(position));
        android.support.v4.app.FragmentManager fragmentManager = ((MainNavigationScreen) context).getSupportFragmentManager();
        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) fragmentManager.findFragmentById(R.id.navigation_drawer);
        stockItemManager = navigationDrawerFragment.getStockItemManager();
        if(position >= stockItemManager.getCount()){
            position--;
            stockItemManager.removeStock(position);
            ((MainNavigationScreen) context).setStockItemManager(stockItemManager);
            notifyItemRemoved(position);
            Toast.makeText(context.getApplicationContext(), "Unable to delete", Toast.LENGTH_SHORT).show();
        }else{
            stockItemManager.removeStock(position);
            ((MainNavigationScreen) context).setStockItemManager(stockItemManager);
            notifyItemRemoved(position);
            notifyDataSetChanged();
        }
    }

    private void touchPosition(int position) {
        int lastPosition = mTouchedPosition;
        mTouchedPosition = position;
        if (lastPosition >= 0)
            notifyItemChanged(lastPosition);
        if (position >= 0)
            notifyItemChanged(position);
    }

    public void setSelectedRow(int position) {
        int lastPosition = mSelectedPosition;
        mSelectedPosition = position;

        /* Required to update the color selection */
        notifyItemChanged(lastPosition);
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        /* Do null check to avoid NullPointerExceptions */
        return stockItemManager != null ? stockItemManager.getCount() : 0;
    }



//    @Override
//    public void onMoveItem(int fromPosition, int toPosition) {
//        Log.d(TAG, "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");
//
//        if (fromPosition == toPosition) {
//            return;
//        }
//
//        stockItemManager.moveItem(fromPosition, toPosition);
//
//        notifyItemMoved(fromPosition, toPosition);
//    }

    public static class ListRecyclerHolder extends RecyclerView.ViewHolder {
        private TextView stockName;
        private TextView stockSymbol;
        private TextView stockPrice;
        private TextView stockChange;
        private TextView stockDayLow;
        private TextView stockDayHigh;
        private View deleteBar;

        public ListRecyclerHolder(View view) {
            super(view);
            stockName  = (TextView) view.findViewById(R.id.stockNameText);
            stockSymbol  = (TextView) view.findViewById(R.id.stockSymbolText);
            stockPrice  = (TextView) view.findViewById(R.id.stockPriceText);
            stockChange  = (TextView) view.findViewById(R.id.stockChangeText);
            stockDayLow  = (TextView) view.findViewById(R.id.stockDayLowText);
            stockDayHigh  = (TextView) view.findViewById(R.id.stockDayHighText);
            deleteBar = view.findViewById(R.id.drag_handle);
        }


    }
}
