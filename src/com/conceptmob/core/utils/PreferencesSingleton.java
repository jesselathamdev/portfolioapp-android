package com.conceptmob.core.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;


public class PreferencesSingleton {
    private static PreferencesSingleton instance;
    private Context context;
    private SharedPreferences settings;
    
    private PreferencesSingleton() {}
    
    public static PreferencesSingleton getInstance() {
        if (instance == null) {
            instance = new PreferencesSingleton();                
        }
        return instance;
    }
    
    public void Initialize(Context context) {
        this.context = context;
        
        this.settings = PreferenceManager.getDefaultSharedPreferences(this.context);
    }
    
    public void setPreference(String key, String value) {
        Editor editor = this.settings.edit();
        editor.putString(key, value);
        editor.commit();
    }
    
    public String getPreference(String key, String defValue) {
        return this.settings.getString(key, defValue);        
    }
    
    public boolean contains(String key) {
        return this.settings.contains(key);
    }
}
