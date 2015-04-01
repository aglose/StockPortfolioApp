package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrew.androiddevelopment.stockportfolioapp.R;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers.StockNewsManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Andrew on 3/28/2015.
 */
public class NewsCardAdapter extends RecyclerView.Adapter<NewsCardAdapter.NewsHolder> {
    private final Context context;
    private StockNewsManager stockNewsManager;

    public NewsCardAdapter(Context context, StockNewsManager stockNewsManager) {
        this.context = context;
        this.stockNewsManager = stockNewsManager;
    }

    @Override
    public NewsCardAdapter.NewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_news_layout, parent, false);
        return new NewsHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsHolder holder, final int position) {
        final WebSettings webSettings = holder.articleContent.getSettings();
        webSettings.setDefaultFontSize(8);
        holder.articleContent.setBackgroundColor(Color.TRANSPARENT);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = stockNewsManager.getStockNewsItem(position).getArticleFullURL();
                Uri uriUrl = Uri.parse(url);
                Log.d("Debug adapter", uriUrl.toString());
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                context.startActivity(launchBrowser);
            }
        });
        holder.articleTitle.setText(String.valueOf(stockNewsManager.getStockNewsItem(position).getArticleTitle()));
        holder.articleContent.loadData(String.valueOf(stockNewsManager.getStockNewsItem(position).getArticleContent()), "text/html", "UTF-8");
        holder.articleURL.setText(String.valueOf(stockNewsManager.getStockNewsItem(position).getArticleURL()));
        holder.publisher.setText(String.valueOf(stockNewsManager.getStockNewsItem(position).getPublisher()));
        holder.articleDate.setText(String.valueOf(stockNewsManager.getStockNewsItem(position).getPublisherDate()));
        if(stockNewsManager.getStockNewsItem(position).getImageURL().length() != 0){
            Picasso.with(context).load(stockNewsManager.getStockNewsItem(position).getImageURL()).into(holder.imageView);
        }else{
            holder.imageView.setMinimumWidth(500);
            holder.imageView.setBackgroundColor(context.getResources().getColor(R.color.Fuchsia));
        }

    }

    @Override
    public int getItemCount() {
        return stockNewsManager.getCount();
    }


    public static class NewsHolder extends RecyclerView.ViewHolder {
        TextView articleTitle;
        WebView articleContent;
        TextView articleURL;
        TextView publisher;
        TextView articleDate;
        ImageView imageView;
        CardView cardView;

        public NewsHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            articleTitle = (TextView) itemView.findViewById(R.id.newsArticleTitleText);
            articleContent = (WebView) itemView.findViewById(R.id.newsArticleContentText);
            articleURL = (TextView) itemView.findViewById(R.id.newsArticleLinkText);
            publisher = (TextView) itemView.findViewById(R.id.newsArticlePublisherText);
            articleDate = (TextView) itemView.findViewById(R.id.dateOfArticleText);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
}
