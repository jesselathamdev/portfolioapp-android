package com.conceptmob.portfolioapp.core;

import com.conceptmob.portfolioapp.PortfoliosActivity;
import com.conceptmob.portfolioapp.R;
import com.conceptmob.portfolioapp.SimpleActivity;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.ActionBar.OnNavigationListener;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;


public class BaseListActivity extends ListActivity {

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     
        setupNavigation();        
    }
    
    
    // ###################################################################################################################
    // setupNavigation
    // ###################################################################################################################
    
    private void setupNavigation() {
        getActionBar().setDisplayShowTitleEnabled(false);
        SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.action_list, android.R.layout.simple_spinner_dropdown_item);
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        
        getActionBar().setListNavigationCallbacks(spinnerAdapter, new OnNavigationListener() {
            
            private boolean firstHit = true;
            
            @Override
            public boolean onNavigationItemSelected(int position, long id) {
                if (firstHit) {
                    firstHit = false;
                    return true;
                }
                
                Intent intent;
                
                switch (position) {
                    case 0:     // portfolios
                        intent = new Intent(BaseListActivity.this, PortfoliosActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        BaseListActivity.this.startActivity(intent);
                        BaseListActivity.this.finish();
                        
                        break;
                    case 1:     // transaction activity
                        intent = new Intent(BaseListActivity.this, SimpleActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        BaseListActivity.this.startActivity(intent);                        
                        BaseListActivity.this.finish();
                        
                        break;
                }   
                    
                return true;
            }
            
        });
    }
    
}