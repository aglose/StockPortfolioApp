package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andrew.androiddevelopment.stockportfolioapp.R;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers.RegularNewsManager;

import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.view.CardView;

/**
 * Created by Andrew on 4/3/2015.
 */
public class ExpandableCard extends CardExpand {
    private static final int[] COLORS = new int[] { 0xFF33B5E5, 0xFFAA66CC, 0xFF99CC00, 0xFFFFBB33, 0xFFFF4444 };
    private RegularNewsManager regularNewsManager;
    private int count;

    public ExpandableCard(Context context) {
        super(context, R.layout.test_list_card_inner_layout);
    }

    public ExpandableCard(Context context, RegularNewsManager regularNewsManager, int i) {
        super(context, R.layout.test_list_card_inner_layout);
        count = i;
        this.regularNewsManager = regularNewsManager;
    }


    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        if (view == null) return;

        LinearLayout ka = (LinearLayout) view;
        ka.setBackgroundColor(COLORS[count % 5]);
        TextView articleContent = (TextView) view.findViewById(R.id.regularNewsContent);

        if (articleContent != null) {
            articleContent.setText(String.valueOf(regularNewsManager.getRegularNewsItem(count).getArticleContent()));
        }

    }
}