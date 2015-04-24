package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrew.androiddevelopment.stockportfolioapp.R;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers.RegularNewsManager;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Andrew on 4/3/2015.
 */
public class RegularNewsCard extends Card {
    private RegularNewsManager regularNewsManager;
    private static final int[] COLORS = new int[] { 0xFF33B5E5, 0xFFAA66CC, 0xFF99CC00, 0xFFFFBB33, 0xFFFF4444 };
    private View cardColor;
    private TextView articleTitle;
    private TextView articleContent;
    private TextView articleSource;
    private TextView articleNewsDate;
    private ImageButton sourceIcon;
    private int position;
    private int[]  colorWheel;
    private Context context;

    public RegularNewsCard(Context context, RegularNewsManager regularNewsManager, int position) {
        super(context, R.layout.fragment_regular_news_layout);
        this.context = context;
        this.regularNewsManager = regularNewsManager;
        this.position = position;
        Resources res = context.getResources();
        colorWheel = res.getIntArray(R.array.colorWheel);
    }


    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        if (view != null) {
            cardColor = view.findViewById(R.id.colorOfCard);
            articleTitle = (TextView) view.findViewById(R.id.regularNewsTitle);
            articleContent = (TextView) view.findViewById(R.id.regularNewsContent);
            articleSource = (TextView) view.findViewById(R.id.regularNewsSource);
            articleNewsDate = (TextView) view.findViewById(R.id.regularNewsDate);
            sourceIcon = (ImageButton) view.findViewById(R.id.imageButton);


            if (articleTitle != null){

                cardColor.setBackgroundColor(COLORS[position % 5]);


                articleTitle.setText(String.valueOf(regularNewsManager.getRegularNewsItem(position).getArticleTitle()));
                articleSource.setText(String.valueOf(regularNewsManager.getRegularNewsItem(position).getArticleSource()));
                articleNewsDate.setText(String.valueOf(regularNewsManager.getRegularNewsItem(position).getArticleTimePublished()));
                sourceIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.foxbusiness_icon));
                sourceIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = regularNewsManager.getRegularNewsItem(position).getArticleURL();
                        Uri uriUrl = Uri.parse(url);
                        Log.d("Debug adapter", uriUrl.toString());
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        context.startActivity(launchBrowser);
                    }
                });
            }
        }
    }
}
