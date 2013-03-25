package com.conceptmob.porfolioapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;

import com.conceptmob.portfolioapp.R;
import com.conceptmob.portfolioapp.utils.PortfolioAppRestClient;
import com.loopj.android.http.*;


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
        
       PortfolioAppRestClient client = new PortfolioAppRestClient();
       RequestParams params = new RequestParams();
       
       client.get("auth/token/create", params, new AsyncHttpResponseHandler() {
          @Override
          public void onSuccess(String response) {
              
          }
       });
    }
  
}
