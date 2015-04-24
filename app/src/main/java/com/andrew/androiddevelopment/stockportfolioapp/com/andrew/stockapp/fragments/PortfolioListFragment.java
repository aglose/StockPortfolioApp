package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.andrew.androiddevelopment.stockportfolioapp.MainNavigationScreen;
import com.andrew.androiddevelopment.stockportfolioapp.NavigationCallbacks;
import com.andrew.androiddevelopment.stockportfolioapp.R;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers.PortfolioManager;

import org.json.JSONObject;


public class PortfolioListFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    public static PortfolioListFragment newInstance(JSONObject stockInfo) {
        PortfolioListFragment fragment = new PortfolioListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    public PortfolioListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //save items here
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.stock_card_layout, container, false);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    /**
     * Created by Andrew on 3/24/2015.
     */
    public static class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ListRecyclerHolder>{
        private final Context context;
        private int mSelectedPosition;
        private int mTouchedPosition = -1;
        private NavigationCallbacks mNavigationCallbacks;
        private static final String TAG = "MyDraggableItemAdapter";
        private PortfolioManager portfolioManager;
        public static boolean unCheckAll = false;

        public RecyclerAdapter(Context context, PortfolioManager portfolioManager) {
            this.context = context;
            this.portfolioManager = portfolioManager;
        }

        public void setNavigationCallbacks(NavigationCallbacks navigationCallbacks) {
            mNavigationCallbacks = navigationCallbacks;
        }

        @Override
        public ListRecyclerHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.stock_card_layout, viewGroup, false);
            return new ListRecyclerHolder(view);
        }

        @Override
        public void onBindViewHolder(ListRecyclerHolder viewHolder, final int i) {
            viewHolder.stockName.setText(String.valueOf(portfolioManager.getStockItem(i).getName()));
            if (portfolioManager.getStockItem(i).getChange().toString().startsWith("+")) {
                viewHolder.stockPrice.setText(String.valueOf(portfolioManager.getStockItem(i).getLastTradePrice()));
                viewHolder.stockPrice.setTextColor(Color.GREEN);
            } else {
                viewHolder.stockPrice.setText(String.valueOf(portfolioManager.getStockItem(i).getLastTradePrice()));
                viewHolder.stockPrice.setTextColor(Color.RED);
            }
            viewHolder.stockSymbol.setText(String.valueOf(portfolioManager.getStockItem(i).getSymbol()));
            viewHolder.stockChange.setText(String.valueOf(portfolioManager.getStockItem(i).getChange()));
            viewHolder.stockDayLow.setText(String.valueOf(portfolioManager.getStockItem(i).getDaysLow()));
            viewHolder.stockDayHigh.setText(String.valueOf(portfolioManager.getStockItem(i).getDaysHigh()));

            if(unCheckAll){
                viewHolder.checkDelete.setChecked(false);
            }
            /* handle gestures and click events */
            handleRowEvents(viewHolder.stockSelectArea, i);
            handleBatchDelete(viewHolder.checkDelete, i);

            /* Highlight selected row */
            if (mSelectedPosition == i || mTouchedPosition == i) {
                viewHolder.itemView.setBackgroundColor(Color.parseColor("#6257FF"));
            } else {
                viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        private void handleBatchDelete(final CheckBox checkDelete, final int i) {
            checkDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!checkDelete.isChecked()) {
                        Log.d("CheckBox", "checked");
                        checkDelete.setChecked(false);
                        mNavigationCallbacks.itemUnChecked(i);
                    }else{
                        Log.d("CheckBox", "unchecked");
                        checkDelete.setChecked(true);
                        mNavigationCallbacks.itemChecked(i);
                    }
                }
            });
        }

        private void handleRowEvents(final View itemView, final int i) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Debug", "short press");
                    FragmentManager fragmentManager = ((MainNavigationScreen) context).getSupportFragmentManager();
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
            FragmentManager fragmentManager = ((MainNavigationScreen) context).getSupportFragmentManager();
            NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) fragmentManager.findFragmentById(R.id.navigation_drawer);
            portfolioManager = navigationDrawerFragment.getStockItemManager();
            if(position >= portfolioManager.getCount()){
                position--;
                portfolioManager.removeStock(position);
                ((MainNavigationScreen) context).setPortfolioManager(portfolioManager);
                notifyItemRemoved(position);
            }else{
                portfolioManager.removeStock(position);
                ((MainNavigationScreen) context).setPortfolioManager(portfolioManager);
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
            return portfolioManager != null ? portfolioManager.getCount() : 0;
        }

        public void removeAllChecks(boolean set){
            unCheckAll = set;
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
            private CheckBox checkDelete;
            private View stockSelectArea;

            public ListRecyclerHolder(View view) {
                super(view);
                stockName  = (TextView) view.findViewById(R.id.stockNameText);
                stockSymbol  = (TextView) view.findViewById(R.id.stockSymbolText);
                stockPrice  = (TextView) view.findViewById(R.id.stockPriceText);
                stockChange  = (TextView) view.findViewById(R.id.stockChangeText);
                stockDayLow  = (TextView) view.findViewById(R.id.stockDayLowText);
                stockDayHigh  = (TextView) view.findViewById(R.id.stockDayHighText);
                checkDelete = (CheckBox) view.findViewById(R.id.checkBox);
                stockSelectArea = (View) view.findViewById(R.id.stockSelectArea);
            }
        }
    }
}
