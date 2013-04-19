package com.conceptmob.portfolioapp;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.conceptmob.portfolioapp.R;
import com.conceptmob.portfolioapp.core.BaseActivity;


public class SimpleActivity extends BaseActivity {
  
    private TextView tvMessage;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(app.TAG, "ACTIVITY: Loaded Simple");
        
        // set up the associated layout
        setContentView(R.layout.activity_simple);

        initActionBar();
        
        ActionBar actionBar = getActionBar();
        
        
        
        tvMessage = (TextView)findViewById(R.id.tvSimpleActivityLabel01);        
        tvMessage.setText("Hello and welcome to your next challenge!");
    }
}
