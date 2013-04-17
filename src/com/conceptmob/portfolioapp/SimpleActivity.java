package com.conceptmob.portfolioapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.conceptmob.portfolioapp.R;
import com.conceptmob.portfolioapp.core.BaseActivity;
import com.conceptmob.portfolioapp.core.BaseApplication;
import com.conceptmob.portfolioapp.data.AuthTokenContainer;
import com.fasterxml.jackson.databind.ObjectMapper;


public class SimpleActivity extends BaseActivity {
  
    private BaseApplication app;
    private TextView tvMessage;
    private TextView tvMessage2;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // pull app instance details from BaseApplication
        app = (BaseApplication)this.getApplication();
        
        Log.i(app.TAG, "ACTIVITY: Loaded Simple");
        
        // set up the associated layout
        setContentView(R.layout.activity_simple);
        
        tvMessage = (TextView)findViewById(R.id.tvSimpleActivityLabel01);        
        tvMessage.setText("Hello and welcome to your next challenge!");
    }
}
