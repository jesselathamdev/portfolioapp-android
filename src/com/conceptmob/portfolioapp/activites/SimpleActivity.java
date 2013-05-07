package com.conceptmob.portfolioapp.activites;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.conceptmob.portfolioapp.R;
import com.conceptmob.portfolioapp.base.BaseActivity;


public class SimpleActivity extends BaseActivity {
  
    private TextView tvMessage;
    private String message;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(app.TAG, "ACTIVITY: Loaded Simple");
        
        // set up the associated layout
        setContentView(R.layout.activity_simple);

        initActionBar();
        
        message = "Nothing found!";
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            message = extras.getString("portfolio_id");
        }
        
        tvMessage = (TextView)findViewById(R.id.tvSimpleActivityLabel01);
        tvMessage.setText(message);
    }
}
