package com.conceptmob.portfolioapp;

import java.util.UUID;

import com.conceptmob.core.utils.PreferencesSingleton;
import com.conceptmob.portfolioapp.R;
import com.conceptmob.portfolioapp.core.BaseActivity;
import com.conceptmob.portfolioapp.core.BaseApplication;

import android.app.Application;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;

public class SplashActivity extends BaseActivity {

    private BaseApplication app;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // pull app instance details from BaseApplication
        app = (BaseApplication)this.getApplication();
        
        Log.i(app.TAG, "ACTIVITY: Loaded Splash");
        
        // set up the associated layout
        setContentView(R.layout.activity_splash);
        
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().getDecorView().getBackground().setDither(true);
        
        setDefaultPreferences();
        
        // Set time to splash out
        final int splashScreenTimer = 1000;
	
        // Create a thread to show splash screen up to splash time
        Thread splashThread = new Thread() {
            int wait = 0;
            
            @Override
            public void run() {
                try {
                    super.run();
                    
                    // This is a filler; replace this with any background processes that need to run during start up; important to keep this as an async process in a thread
                    // so that it does not interfere with the main UI thread that is responsible for screen loading etc
                    
                    // Use while to get the splash screen time. Use sleep() to increase the wait variable for every 100L
                    while (wait < splashScreenTimer) {
                        sleep(100);
                        wait += 100;
                    }
                } catch (Exception e) {
                    System.out.println("EXC=" + e);                 
                } finally {
                    // Called after splash time's up. Do some action after splash times up. Here we moved to another main activity class
                    startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                    
                    SplashActivity.this.finish();
                }
            }
        };
        splashThread.start();
    }
    
    private void setDefaultPreferences() {
        // sets default preferences that are used elsewhere in the application such as a unique identifier for API calls 
        PreferencesSingleton.getInstance().Initialize(getApplicationContext());
        
        if (!PreferencesSingleton.getInstance().contains("identifier")) {
            final UUID identifier = UUID.randomUUID();
            PreferencesSingleton.getInstance().setPreference("identifier", identifier.toString());
        }
    }
}
