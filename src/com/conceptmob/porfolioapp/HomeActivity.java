package com.conceptmob.porfolioapp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

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
    String response = "";
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		doSomethingUseful();
		
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
	        URL url = null;
	        HttpURLConnection urlConnection = null;
	        InputStream iS = null;
	        InputStreamReader iSR = null;
	        try {
	            url = new URL("http://portfolioapp.conceptmob.com/api/v2/portfolios/");
	            urlConnection = (HttpURLConnection)url.openConnection();
	            iS = new BufferedInputStream(urlConnection.getInputStream());
	            iSR = new InputStreamReader(iS);
	            
	            int data = iSR.read();
	            while (data != -1) {
	                char current = (char) data;
	                data = iSR.read();
	                response += current;
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            urlConnection.disconnect();
	        }
	        
	        return "Complete!";
	    }
	    
	    @Override 
	    protected void onPostExecute(String result) {
	        tvResponse = (TextView)findViewById(R.id.tvHomeResponse);
	        tvResponse.setText(response);
	    }
	}
	
	private void doSomethingUseful() {
	    tvMessage = (TextView)findViewById(R.id.tvHomeMessage);
        tvMessage.setText("Thinking...");
        
        Log.v(this.toString(), "API: Attempting to contact server");
	}
}
