package com.conceptmob.portfolioapp;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;

import com.conceptmob.core.BaseActivity;
import com.conceptmob.core.communication.RestClient;
import com.conceptmob.portfolioapp.R;
import com.loopj.android.http.*;


public class SimpleActivity extends BaseActivity {
  
    private TextView tvMessage;
    private String message = "";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
      
        Log.i(getAppName(), "SCREEN: Loaded Simple activity.");
        
        StrictMode.ThreadPolicy policy = new StrictMode.
        ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); 
      
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
            
        
        
        tvMessage = (TextView)findViewById(R.id.tvSimpleActivityLabel01);
        
        RequestParams params = new RequestParams();
       
        RestClient client = new RestClient();
        
        client.get("auth/token/create?email=user1@conceptmob.com&password=access&identifier=12345", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                try {                    
                    message = response;
                    Log.v("API SUCCESS", "onSuccess: " + response.toString());
                } catch (Exception e) {
                    Log.v("API ERROR", "onError: " + e.toString());
                }                
            }
            
            @Override
            public void onFailure(Throwable e, String response) {                
                Log.v("API ERROR", "onFailure: " + response);
                Log.v("API ERROR", "onFailure: " + e.getMessage());
                Log.v("API ERROR", "onFailure: " + e.toString());
                Log.v("API ERROR", "onFailure: " + e.getCause());
                
                message = response;                
            }
            
            @Override 
            public void onFinish() {
                Log.v("API MESSAGE", "onFinish complete");
                
                tvMessage.setText(message);
            }
        });
   
    }
}
