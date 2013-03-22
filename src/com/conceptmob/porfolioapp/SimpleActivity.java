package com.conceptmob.porfolioapp;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;


public class SimpleActivity extends Activity {
  
    private TextView tvMessage;
    private String message = "";
    private final String baseServerURL = "http://10.0.2.2:8000/api/v2/";
  
    @Override
    public void onCreate(Bundle savedInstanceState) {
      
        StrictMode.ThreadPolicy policy = new StrictMode.
        ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); 
      
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
            
        Log.v("ACTIVITY", "SCREEN: Loaded Simple screen.");
        
        tvMessage = (TextView)findViewById(R.id.tvSimpleActivityLabel01);
        
        URL url = null;
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        
        try {
            url = new URL(baseServerURL + "auth/token/create");
            httpURLConnection = (HttpURLConnection)url.openConnection();
            
            inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                                    
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpURLConnection.disconnect();
        }
    }
  
}
