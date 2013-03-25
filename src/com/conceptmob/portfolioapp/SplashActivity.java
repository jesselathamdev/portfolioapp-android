package com.conceptmob.portfolioapp;

import com.conceptmob.core.BaseActivity;
import com.conceptmob.portfolioapp.R;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;

public class SplashActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        Log.i(getAppName(), "ACTIVITY: Loaded Splash");
        
        // set up the activity and associated layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().getDecorView().getBackground().setDither(true);
        
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
}