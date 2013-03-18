package com.conceptmob.porfolioapp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class PortfoliosActivity extends ListActivity
{
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// new MyAsyncTask().execute("http://10.0.2.2:8000/api/v2/portfolios/");
		new MyAsyncTask().execute("http://portfolioapp.conceptmob.com/api/v2/portfolios/");	    
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	class MyAsyncTask extends AsyncTask<String, Integer, ArrayList<HashMap<String, String>> > {
	    ArrayList<HashMap<String, String>> portfoliosList;
	    
	    @Override
	    protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
	        portfoliosList = processPortfolioJSON(getJSONfromURL(params[0]));
	        
	        return portfoliosList;
	    }
	    
	    @Override 
	    protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
	        ListAdapter adapter = new SimpleAdapter(PortfoliosActivity.this, portfoliosList, R.layout.list_portfolios, new String[] { "name", "portfolio_id" }, new int[] { R.id.item_title, R.id.item_subtitle });
	        PortfoliosActivity.this.setListAdapter(adapter);
	        final ListView lv = getListView();
	        lv.setTextFilterEnabled(true);
	        
	    }
	}
		
	
	private ArrayList<HashMap<String, String>> processPortfolioJSON(JSONObject json) {
	    
	    ArrayList<HashMap<String, String>> portfoliosList = new ArrayList<HashMap<String, String>>();
	    
	    try {
	        JSONArray portfolios = json.getJSONObject("response").getJSONArray("portfolios");
	        
	        for (int i = 0; i < portfolios.length(); i++) {
	            HashMap<String, String> map = new HashMap<String, String>();
	            JSONObject p = portfolios.getJSONObject(i);
	            map.put("id", String.valueOf(i));
	            map.put("name", p.getString("name"));
	            map.put("portfolio_id", p.getString("id".toString()));
	            portfoliosList.add(map);
	        }	        
	    } catch (JSONException e) {
	        Log.e("log_tag", "Error parsing portfolio JSON: " + e.toString());
	    }
	    
        return portfoliosList;
	}
	
	
	public static JSONObject getJSONfromURL(String url) {
	    URL _url = null;
        HttpURLConnection _urlConnection = null;
        InputStream _iS = null;
        InputStreamReader _iSR = null;
        StringBuilder _result = new StringBuilder();
        
        try {
            _url = new URL(url); 
            _urlConnection = (HttpURLConnection)_url.openConnection();
            _iS = new BufferedInputStream(_urlConnection.getInputStream());
            _iSR = new InputStreamReader(_iS);
            
            int data = _iSR.read();
            while (data != -1) {
                char current = (char) data;
                data = _iSR.read();
                _result.append(current);
            }
        } catch (Exception e) {            
            Log.e("Downloader", "Error getting data from server: " + e.toString());
        } finally {
            _urlConnection.disconnect();
        }
        
        JSONObject _responseJO = null;
        
        try {
            _responseJO = new JSONObject(_result.toString());            
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data from server: " + e.toString());
        }
	    
	    return _responseJO;
	}

}