package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.items;

import android.util.Log;

import org.w3c.dom.Node;

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Created by Andrew on 3/28/2015.
 */
public class StockNewsItem {
    private String articleTitle = "";
    private String articleContent = "";
    private String articleURL = "";
    private String articleFullURL = "";
    private String publisher = "";
    private String imageURL = "";
    private String publishedDate = "";

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getArticleContent() {
        return articleContent;
    }

    public void setArticleContent(String articleContent) {
        String localContent;

        if(articleContent.length() > 300){
            StringBuilder myName = new StringBuilder(articleContent);
            String newString = myName.substring(0, 300);
            StringBuilder finalString = new StringBuilder(newString);
            finalString.append("...");
            localContent = finalString.toString();
        }else{
            localContent = articleContent;
        }
        this.articleContent = localContent;
    }

    public String getArticleURL() {
        return articleURL;
    }

    public void setArticleURL(String articleURL) {
        String localURL = "";
        String colon = "\u00253A";
        String forwardslash = "\u00252F";

        articleURL = articleURL.replaceAll(colon, ":");
        articleURL = articleURL.replaceAll(forwardslash, "/");

        setArticleFullURL(articleURL);

        if(articleURL.length() > 40){
            StringBuilder myName = new StringBuilder(articleURL);
            String newString = myName.substring(0, 40);
            StringBuilder finalString = new StringBuilder(newString);
            finalString.append("...");
            localURL = finalString.toString();
        }else{
            localURL = articleURL;
        }
        this.articleURL = localURL;
    }

    public String getArticleFullURL() {
        return articleFullURL;
    }

    public void setArticleFullURL(String articleFullURL) {
        this.articleFullURL = articleFullURL;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        String localTitle = "";
        if(articleTitle.length() > 30){
            String apostrophe = "&#39;";
            Log.d("Debug string", apostrophe);
            articleTitle = articleTitle.replaceAll(apostrophe, "'");

            StringBuilder myName = new StringBuilder(articleTitle);
            String newString = myName.substring(0, 30);
            StringBuilder finalString = new StringBuilder(newString);
            finalString.append("...");
            localTitle = finalString.toString();
        }else{
            localTitle = articleTitle;
        }
        this.articleTitle = localTitle;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setPublisherDate(String publishedDate) {

        StringBuilder myName = new StringBuilder(publishedDate);
        int index = myName.indexOf(":");
        String choppedDate = myName.substring(0, index-3);

        this.publishedDate = choppedDate;
    }

    public String getPublisherDate() {
        return publishedDate;
    }
}
