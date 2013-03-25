package com.conceptmob.core;

import android.app.Activity;


public class BaseActivity extends Activity {

    private String _appName = "PortfolioApp";  // getString(R.string.app_name); see http://stackoverflow.com/questions/6261990/strange-nullpointerexception to fix nullpointerexception when using getString before the getContext with an activity goes off
    
    public String getAppName() {
        return _appName;
    }    
}
