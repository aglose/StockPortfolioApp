package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.downloadertasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.andrew.androiddevelopment.stockportfolioapp.MainNavigationScreen;
import com.andrew.androiddevelopment.stockportfolioapp.R;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.adapters.RegularNewsCardAdapter;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.fragments.RegularNewsFragment;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.items.RegularNewsItem;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers.RegularNewsManager;

import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import it.gmariotti.cardslib.library.view.CardListView;

/**
 * Created by Andrew on 4/2/2015.
 */
public class RegularNewsDownloaderTask extends AsyncTask<Void, Void, Void> {
    private String articleTitle = "";
    private String articleSource = "";
    private String articleTimePublished = "";
    private String articleContent = "";
    private String articleURL = "";
    private Activity activity;
    private CardListView listView;
    private RegularNewsCardAdapter regularNewsCardAdapter;
    private RegularNewsManager regularNewsManager;
    private View rootView;
    private boolean refresh;

    public RegularNewsDownloaderTask(Activity activity, RegularNewsManager regularNewsManager, View rootView, boolean refresh){
        this.activity = activity;
        this.regularNewsManager = regularNewsManager;
        this.rootView = rootView;
        this.refresh = refresh;
    }
    @Override
    protected Void doInBackground(Void... params) {
        getMarketNews();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        RegularNewsFragment fragment = (RegularNewsFragment) ((MainNavigationScreen) activity).getSupportFragmentManager().findFragmentByTag("RegularNewsFragment");
        fragment.initCards(regularNewsManager);
        listView = (CardListView) rootView.findViewById(R.id.list_expand_view);

        if(refresh){
            SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setRefreshing(false);
            listView.setVisibility(View.VISIBLE);
        }

        Log.d("Debug ", "completed task");
    }

    private void getMarketNews(){
        Document doc = null;
        try {
            String url = "https://www.google.com/finance/market_news";

            doc = Jsoup.parse(new URL(url).openStream(), "ISO-8859-15", url);;

        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements articleTitleElement = doc.select("#news-main .name a");
        Elements articleSourceElement = doc.select("#news-main .byLine .src");
        Elements articleTimePublishedElement = doc.select("#news-main .date");
        Elements articleContentElement;

        Iterator itTitle = articleTitleElement.iterator();
        Iterator itSource = articleSourceElement.iterator();
        Iterator itTime = articleTimePublishedElement.iterator();

        for (int i = 0; i<articleTitleElement.size();i++) {
            int article = i+1;
            articleContentElement = doc.select("#news-main #Article"+article+" div");

            Element title = (Element) itTitle.next();
            Element source = (Element) itSource.next();
            Element time = (Element) itTime.next();

            if (title.hasText()) {
                articleTitle = title.text();
                articleURL = title.attr("href");
            }
            if (source.hasText()) {
                articleSource = source.text();
            }
            if (time.hasText()) {
                articleTimePublished = time.text();
            }
            if (articleContentElement.hasText()) {
                articleContent = articleContentElement.text();
            }

            RegularNewsItem newNewsItem = new RegularNewsItem();
            newNewsItem.setArticleTitle(articleTitle);
            newNewsItem.setArticleSource(articleSource);
            newNewsItem.setArticleTimePublished(articleTimePublished);
            newNewsItem.setArticleContent(articleContent);
            newNewsItem.setArticleURL(articleURL);

            regularNewsManager.addStockNewsItem(newNewsItem);

        }


    }

}
