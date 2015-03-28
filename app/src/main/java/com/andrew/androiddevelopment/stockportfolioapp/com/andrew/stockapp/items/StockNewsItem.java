package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.items;

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
    private String publisher = "";

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

        if(articleContent.length() > 200){
            StringBuilder myName = new StringBuilder(articleContent);
            String newString = myName.substring(0, 202);
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
        if(articleURL.length() > 30){
            StringBuilder myName = new StringBuilder(articleURL);
            String newString = myName.substring(0, 32);
            StringBuilder finalString = new StringBuilder(newString);
            finalString.append("...");
            localURL = finalString.toString();
        }else{
            localURL = articleURL;
        }
        this.articleURL = localURL;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        String localTitle = "";
        if(articleTitle.length() > 30){
            StringBuilder myName = new StringBuilder(articleTitle);
            String newString = myName.substring(0, 32);
            StringBuilder finalString = new StringBuilder(newString);
            finalString.append("...");
            localTitle = finalString.toString();
        }else{
            localTitle = articleTitle;
        }
        this.articleTitle = localTitle;
    }
}
