package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.items;

import android.util.Log;

/**
 * Created by Andrew on 4/1/2015.
 */
public class RegularNewsItem {
    private String articleTitle = "";
    private String articleSource = "";
    private String articleTimePublished = "";
    private String articleContent = "";
    private String articleURL = "";

    public String getArticleContent() {
        return articleContent;
    }

    public void setArticleContent(String articleContent) {
        this.articleContent = articleContent;
    }

    public String getArticleURL() {
        return articleURL;
    }

    public void setArticleURL(String articleURL) {
        this.articleURL = articleURL;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public String getArticleSource() {
        return articleSource;
    }

    public void setArticleSource(String articleSource) {
        this.articleSource = articleSource;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public void setArticleTimePublished(String articleTimePublished) {
        this.articleTimePublished = articleTimePublished;
    }

    public String getArticleTimePublished() {
        return articleTimePublished;
    }
}
