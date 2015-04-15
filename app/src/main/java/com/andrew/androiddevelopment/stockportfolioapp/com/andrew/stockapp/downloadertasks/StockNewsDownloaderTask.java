package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.downloadertasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.andrew.androiddevelopment.stockportfolioapp.MainNavigationScreen;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.adapters.StockNewsCardAdapter;
import com.andrew.androiddevelopment.stockportfolioapp.R;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.fragments.StockNewsDisplayFragment;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers.StockNewsManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Andrew on 3/28/2015.
 */
public class StockNewsDownloaderTask  extends AsyncTask<String, Void, Void> {
    private static final String XML_ITEM = "item";
    private JSONObject jArray = null;
    private InputStream is = null;
    private String result = null;
    private Context context = null;
    private StockNewsManager stockNewsManager = new StockNewsManager();
    private View rootView;
    private RecyclerView newsListRecycler;
    private StockNewsCardAdapter stockNewsCardAdapter;
    private static String symbol;
    private String newsSource = null;
    private String newsCopyright = null;

    public StockNewsDownloaderTask(Context context,  View rootView, String symbol){
        this.context = context;
        this.rootView = rootView;
        this.symbol = symbol;
    }

    @Override
    protected Void doInBackground(String... url) {
        String query = url[0];
        String preference = url[1];

        getStockNews(query, preference);

        return null;
    }

    protected void onProgressUpdate(Void... progress) {
        TextView title = (TextView) rootView.findViewById(R.id.companyTitleNewsText);
        TextView copyright = (TextView) rootView.findViewById(R.id.copyrightText);

        title.setText(newsSource);
        copyright.setText(newsCopyright);
    }


    @Override
    protected void onPostExecute(Void result) {
        FragmentManager fragmentManager = ((MainNavigationScreen) context).getSupportFragmentManager();
        StockNewsDisplayFragment fragment = (StockNewsDisplayFragment) fragmentManager.findFragmentByTag("StockNewsFragment");
        fragment.crossfadeViews();

        newsListRecycler = (RecyclerView) rootView.findViewById(R.id.newsList);
        stockNewsCardAdapter = new StockNewsCardAdapter(context, stockNewsManager);
        newsListRecycler.setAdapter(stockNewsCardAdapter);

        Switch aSwitch = (Switch) rootView.findViewById(R.id.switchNews);
        aSwitch.setVisibility(View.VISIBLE);

    }

    private void getStockNews(String query, String preference){
        if(preference.equalsIgnoreCase("false")){
            getYahooNews(symbol);
        }else {
            Log.d("Debug url", query);
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(query);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
            } catch (Exception e) {
                Log.e("log_tag", "Error in http connection " + e.toString());
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                result = sb.toString();
            } catch (Exception e) {
                Log.e("log_tag", "Error converting result " + e.toString());
            }

            boolean googleNotWorking = false;

            try {
                jArray = new JSONObject(result);
                googleNotWorking = Integer.valueOf(jArray.getJSONObject("query").getString("count").trim()) == 0;
            } catch (JSONException e) {
                Log.e("log_tag", "Error parsing data " + e.toString());
            }

            Log.d("Debug jArray", jArray.toString());
            Log.d("Debug jArray", preference);

            if (preference.equalsIgnoreCase("true")) {
                if (googleNotWorking) {
                    newsSource = context.getString(R.string.issueGoogleNews);
                    newsCopyright = "";
                    publishProgress();
                } else {
                    getGoogleNews();
                }
            } else {//TRY AND GET YAHOO NEWS
                getYahooNews(symbol);
            }
        }
    }

    private void getGoogleNews() {
        newsSource = context.getString(R.string.googleNews);
        newsCopyright = context.getString(R.string.copyrightGoogle);
        publishProgress();
        JSONObject results = null;

        try {
            results = jArray.getJSONObject("query").getJSONObject("results");

            Log.d("Debug count", jArray.getJSONObject("query").getString("count"));

            if(results.get("results") instanceof JSONArray){
                int i = 0;
                JSONArray quotesArray = results.getJSONArray("results");
                while(i != quotesArray.length()){
                    JSONObject newsArticleJSON = quotesArray.getJSONObject(i);
                    Log.d("Debug task", newsArticleJSON.toString());
                    stockNewsManager.addStockNewsItem(newsArticleJSON);
                    i++;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getYahooNews(String symbol){
        newsSource = context.getString(R.string.yahooNews);
        newsCopyright = context.getString(R.string.copyrightYahoo);
        publishProgress();
        String url = "http://finance.yahoo.com/rss/headline?s="+symbol;

        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        }catch(Exception e){
            Log.e("log_tag", "Error in http connection " + e.toString());
        }

        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result=sb.toString();
        }catch(Exception e){
            Log.e("log_tag", "Error converting result "+e.toString());
        }

        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(result));
            doc = db.parse(is);

        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage());
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage());
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
        }

        NodeList nl = doc.getElementsByTagName(XML_ITEM);

        stockNewsManager.addStockNewsItem(nl);
    }
}
