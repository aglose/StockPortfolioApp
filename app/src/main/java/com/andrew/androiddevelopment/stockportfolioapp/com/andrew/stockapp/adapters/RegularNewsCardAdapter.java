package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.andrew.androiddevelopment.stockportfolioapp.R;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers.RegularNewsManager;

import it.gmariotti.cardslib.library.internal.CardExpand;

/**
 * Created by Andrew on 4/1/2015.
 */
public class RegularNewsCardAdapter extends RecyclerView.Adapter<RegularNewsCardAdapter.RegularNewsHolder>{
    private RegularNewsManager regularNewsManager;
    private Context context;
    private int[]  colorWheel;

    public RegularNewsCardAdapter(Context context, RegularNewsManager regularNewsManager) {
        Resources res = context.getResources();
        colorWheel = res.getIntArray(R.array.colorWheel);
        this.regularNewsManager = regularNewsManager;
        this.context = context;
    }

    @Override
    public RegularNewsCardAdapter.RegularNewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_regular_news_layout, parent, false);
        return new RegularNewsHolder(view);
    }

    @Override
    public void onBindViewHolder(final RegularNewsCardAdapter.RegularNewsHolder holder, final int position) {
        holder.cardColor.setBackgroundColor(colorWheel[position%5]);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = regularNewsManager.getRegularNewsItem(position).getArticleURL();
                Uri uriUrl = Uri.parse(url);
                Log.d("Debug adapter", uriUrl.toString());
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                context.startActivity(launchBrowser);
            }
        });

        holder.articleTitle.setText(String.valueOf(regularNewsManager.getRegularNewsItem(position).getArticleTitle()));
        holder.articleContent.setText(String.valueOf(regularNewsManager.getRegularNewsItem(position).getArticleContent()));
        holder.articleSource.setText(String.valueOf(regularNewsManager.getRegularNewsItem(position).getArticleSource()));
        holder.articleNewsDate.setText(String.valueOf(regularNewsManager.getRegularNewsItem(position).getArticleTimePublished()));
        holder.sourceIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.abc_btn_check_material));
    }

    @Override
    public int getItemCount() {
        return regularNewsManager.getCount();
    }

    public class RegularNewsHolder extends RecyclerView.ViewHolder {
        View cardColor;
        TextView articleTitle;
        TextView articleContent;
        TextView articleSource;
        TextView articleNewsDate;
        ImageButton sourceIcon;
        CardView cardView;

        public RegularNewsHolder(View itemView) {
            super(itemView);

            cardColor = itemView.findViewById(R.id.colorOfCard);
            articleTitle = (TextView) itemView.findViewById(R.id.regularNewsTitle);
            articleSource = (TextView) itemView.findViewById(R.id.regularNewsSource);
            articleNewsDate = (TextView) itemView.findViewById(R.id.regularNewsDate);
            sourceIcon = (ImageButton) itemView.findViewById(R.id.imageButton);
        }
    }
}
