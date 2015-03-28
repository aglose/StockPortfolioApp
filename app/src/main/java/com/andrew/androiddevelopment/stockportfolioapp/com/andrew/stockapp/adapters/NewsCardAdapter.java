package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.andrew.androiddevelopment.stockportfolioapp.R;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers.StockNewsManager;

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
    public void onBindViewHolder(NewsHolder holder, int position) {
        final WebSettings webSettings = holder.articleContent.getSettings();
        webSettings.setDefaultFontSize(8);
        holder.articleContent.setBackgroundColor(Color.TRANSPARENT);

        holder.articleTitle.setText(String.valueOf(stockNewsManager.getStockNewsItem(position).getArticleTitle()));
        holder.articleContent.loadData(String.valueOf(stockNewsManager.getStockNewsItem(position).getArticleContent()), "text/html", "UTF-8");
        holder.articleURL.setText(String.valueOf(stockNewsManager.getStockNewsItem(position).getArticleURL()));
        holder.publisher.setText(String.valueOf(stockNewsManager.getStockNewsItem(position).getPublisher()));
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

        public NewsHolder(View itemView) {
            super(itemView);

            articleTitle = (TextView) itemView.findViewById(R.id.newsArticleTitleText);
            articleContent = (WebView) itemView.findViewById(R.id.newsArticleContentText);
            articleURL = (TextView) itemView.findViewById(R.id.newsArticleLinkText);
            publisher = (TextView) itemView.findViewById(R.id.newsArticlePublisherText);
        }
    }
}
