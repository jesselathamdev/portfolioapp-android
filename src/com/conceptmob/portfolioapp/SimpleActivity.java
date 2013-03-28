package com.conceptmob.portfolioapp;

import java.io.IOException;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.conceptmob.portfolioapp.R;
import com.conceptmob.portfolioapp.data.AuthTokenContainer;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class SimpleActivity extends BaseActivity {
  
    private TextView tvMessage;
    private TextView tvMessage2;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
      
        Log.i(getAppName(), "SCREEN: Loaded Simple activity.");
      
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        
        Intent intent = getIntent();
        String authTokenResponse = intent.getStringExtra("authTokenResponse");
        
        ObjectMapper mapper = new ObjectMapper();
        AuthTokenContainer authTokenContainer = null;
        
        try {
            authTokenContainer = mapper.readValue(authTokenResponse, AuthTokenContainer.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        tvMessage = (TextView)findViewById(R.id.tvSimpleActivityLabel01);        
        tvMessage.setText("Hello " + authTokenContainer.response.user.firstName + ", and welcome to your next challenge!");
        
        tvMessage2 = (TextView)findViewById(R.id.tvSimpleActivityLabel02);        
        tvMessage2.setText("Original response: " + authTokenResponse);
    }
}
