package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers;

import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.items.StockNewsItem;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * Created by Andrew on 3/28/2015.
 */
public class StockNewsManager {
    static final String JSON_TITLE = "titleNoFormatting";
    static final String JSON_CONTENT = "content";
    static final String JSON_URL = "url";
    static final String JSON_PUBLISHER = "publisher";
    private static final String XML_TITLE = "title";
    private static final String XML_CONTENT = "description";
    private static final String XML_URL = "link";
    private static final String XML_DATE = "pubDate";

    private ArrayList<StockNewsItem> stockNewsItemArrayList = new ArrayList<>();

    public int getCount(){
        return stockNewsItemArrayList.size();
    }

    public void addStockNewsItem(JSONObject newsArticleJSON){
        StockNewsItem newItem = createNewsFromJSON(newsArticleJSON);
        stockNewsItemArrayList.add(newItem);
    }

    private StockNewsItem createNewsFromJSON(JSONObject newsArticleJSON){
        StockNewsItem newStockNewsItem = new StockNewsItem();
        try {
            newStockNewsItem.setArticleTitle(newsArticleJSON.getString(JSON_TITLE));
            newStockNewsItem.setArticleContent(newsArticleJSON.getString(JSON_CONTENT));
            newStockNewsItem.setArticleURL(newsArticleJSON.getString(JSON_URL));
            newStockNewsItem.setPublisher(newsArticleJSON.getString(JSON_PUBLISHER));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newStockNewsItem;
    }

    public StockNewsItem getStockNewsItem(int index){
        return stockNewsItemArrayList.get(index);
    }

    public void addStockNewsItem(NodeList list) {
        String title = "";
        String content = "";
        String url = "";
        String pubDate = "";

        for (int i = 0; i < list.getLength(); i++) {
            StockNewsItem newStockNewsItem = new StockNewsItem();
            Element e = (Element) list.item(i);
            title = getValue(e, XML_TITLE);
            content = getValue(e, XML_CONTENT);
            url = getValue(e, XML_URL);
            pubDate = getValue(e, XML_DATE);

            newStockNewsItem.setArticleTitle(title);
            newStockNewsItem.setArticleContent(content);
            newStockNewsItem.setArticleURL(url);
            newStockNewsItem.setPublisher(pubDate);

            stockNewsItemArrayList.add(newStockNewsItem);
        }

    }

    public String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }

    public final String getElementValue( Node elem ) {
        Node child;
        if( elem != null){
            if (elem.hasChildNodes()){
                for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
                    if( child.getNodeType() == Node.TEXT_NODE  ){
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }
}
