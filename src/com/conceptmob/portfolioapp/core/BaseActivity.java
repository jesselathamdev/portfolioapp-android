package com.conceptmob.portfolioapp.core;

import com.conceptmob.core.utils.PreferencesSingleton;
import com.conceptmob.portfolioapp.PortfoliosActivity;
import com.conceptmob.portfolioapp.R;
import com.conceptmob.portfolioapp.SignInActivity;
import com.conceptmob.portfolioapp.SimpleActivity;
import com.conceptmob.portfolioapp.TransactionActivityActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.OnNavigationListener;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class BaseActivity extends Activity {
 
    public BaseApplication app;
    
    
    // ###################################################################################################################
    // onCreate
    // ###################################################################################################################
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // pull app instance details from BaseApplication
        app = (BaseApplication)this.getApplication();
    }
    
    
    // ###################################################################################################################
    // initNavigation
    // ###################################################################################################################
    
    public void initActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        
        // actionBar.getThemedContext() is taken from https://code.google.com/p/android/issues/detail?id=25624 when using dark themes and the text not appearing correctly
        SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(actionBar.getThemedContext(), R.array.action_list, android.R.layout.simple_spinner_dropdown_item);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        
        actionBar.setListNavigationCallbacks(spinnerAdapter, new OnNavigationListener() {
            
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
                        app.currentNavigationItem = 0; 
                        
                        intent = new Intent(getApplicationContext(), PortfoliosActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        
                        break;
                    case 1:     // transaction activity
                        app.currentNavigationItem = 1;
                        
                        intent = new Intent(getApplicationContext(), TransactionActivityActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);                        
                        finish();
                        
                        break;
                }   
                    
                return false;
            }
            
        });
        
        // set the actionbar nagivation spinner to the last known value, otherwise it resets with the above code
        actionBar.setSelectedNavigationItem(app.currentNavigationItem);
    }
    
    
    // ###################################################################################################################
    // Menus
    // ###################################################################################################################
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                // Settings code:
                // 1. Launch new activity
                Toast.makeText(getBaseContext(), "Settings clicked", Toast.LENGTH_SHORT).show();
                
                return true;
            case R.id.menu_signout:
                // Sign Out code:
                // 1. Destroy auth token on server (do later)
                // 2. Remove auth token from shared preferences
                // 3. Close out this activity and launch Sign In page
                
                // clear out auth token
                PreferencesSingleton.getInstance().setPreference("authToken", null);
                
                // start activity
                Intent intent = new Intent(BaseActivity.this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }   
}
