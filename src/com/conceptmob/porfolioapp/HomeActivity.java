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
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class HomeActivity extends Activity
{
    private TextView tvMessage;
    private TextView tvResponse;
    private TextView tvCompleted;
    String readPortfolios = "";
    String stringCompleted = "";
    JSONObject jObj = null;
    List<Portfolio> portfolios = new ArrayList<Portfolio>();
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		tvMessage = (TextView)findViewById(R.id.tvHomeMessage);
        tvMessage.setText("Thinking...");
        
        Log.v(this.toString(), "API: Attempting to contact server");
		
		new FetchData().execute();
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	private class FetchData extends AsyncTask<String, Void, String> {
	   
	    @Override
	    protected String doInBackground(String... params) {
	        readPortfolios = readPortfolios();
	        stringCompleted = parseJSON(readPortfolios());
	        
	        return "Complete!";
	    }
	    
	    @Override 
	    protected void onPostExecute(String result) {
	        tvResponse = (TextView)findViewById(R.id.tvHomeResponse);
	        tvResponse.setText(readPortfolios);
	        
	        tvCompleted = (TextView)findViewById(R.id.tvHomeCompleted);
	        tvCompleted.setText(stringCompleted);
	    }
	}
	
	
	private String readPortfolios() {
	    URL url = null;
        HttpURLConnection urlConnection = null;
        InputStream iS = null;
        InputStreamReader iSR = null;
        StringBuilder builder = new StringBuilder();
        try {
            url = new URL("http://10.0.2.2:8000/api/v2/portfolios/"); // internal loopback test from http://developer.android.com/tools/devices/emulator.html#networkaddresses
//            url = new URL("http://portfolioapp.conceptmob.com/api/v2/portfolios/");
            urlConnection = (HttpURLConnection)url.openConnection();
            iS = new BufferedInputStream(urlConnection.getInputStream());
            iSR = new InputStreamReader(iS);
            
            int data = iSR.read();
            while (data != -1) {
                char current = (char) data;
                data = iSR.read();
                builder.append(current);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("Downloader", "Error getting data from server" + e.toString());
        } finally {
            urlConnection.disconnect();
        }
        
        return builder.toString();	    
	}
	
	private String parseJSON(String json) {
	    JSONObject responseJO = null;	    
	    JSONArray portfoliosJA = null;
	    JSONObject portfolioJO = null;
	    List<Portfolio> portfolios = new ArrayList<Portfolio>();
	    Portfolio portfolio; 
	    
	    try {
	        responseJO = new JSONObject(json);
	        portfoliosJA = responseJO.getJSONObject("data").getJSONArray("portfolios");
	        for (int i = 0; i < portfoliosJA.length(); i++) {
	            portfolioJO = portfoliosJA.getJSONObject(i);
	            portfolio = new Portfolio(portfolioJO.getInt("id"), portfolioJO.getString("name"));
	            portfolios.add(portfolio);	            
	        }
	        
	    } catch (JSONException e) {
	        e.printStackTrace();
	    }
	    
	    return portfolios.get(0).getName().toString();
	}
	
	
	private class Portfolio {
	    int _id;
	    String _name;
	    
	    public Portfolio(int id, String name) {
	        _id = id;
	        _name = name;
	    }
	    
	    public void setId(int id) { _id = id; }     
	    public int getId() { return _id; }      
	    
	    public void setName(String name) { _name = name; }      
	    public String getName() { return _name; }
	}

}

