package com.conceptmob.portfolioapp.core;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.params.HttpParams;

import android.app.Application;
import android.net.http.AndroidHttpClient;
import android.util.Log;

public class BaseApplication extends Application {

    public String TAG = "PortfolioApp";
    public String BASE_URL = "http://10.0.2.2:8000/api/v2/";
    private AndroidHttpClient httpClient;
    
    
    @Override
    public void onCreate() {
        super.onCreate();
        httpClient = createHttpClient();
    }
    
    
    @Override 
    public void onLowMemory() {
        super.onLowMemory();
        shutdownHttpClient();
    }
    
    
    @Override
    public void onTerminate() {
        super.onTerminate();
        shutdownHttpClient();
    }
    
    
    private AndroidHttpClient createHttpClient() {
        AndroidHttpClient httpClient = AndroidHttpClient.newInstance(System.getProperty("http.agent"));
        
        ClientConnectionManager connManager = httpClient.getConnectionManager();
        SchemeRegistry schemeReg = connManager.getSchemeRegistry();
        
        // set extra detfaults here such as timeouts etc
        
        for (String scheme : schemeReg.getSchemeNames()) {
            Log.v(TAG,"Scheme: " + scheme
                + ", port: " + schemeReg.getScheme(scheme).getDefaultPort()
                + ", factory: " + schemeReg.getScheme(scheme).getSocketFactory().getClass().getName());
        }
        
        HttpParams params = httpClient.getParams();
        Log.v(TAG,"http.protocol.version: "+ params.getParameter("http.protocol.version"));
        Log.v(TAG,"http.protocol.content-charset: "+ params.getParameter("http.protocol.content-charset"));
        Log.v(TAG,"http.protocol.handle-redirects: "+ params.getParameter("http.protocol.handle-redirects"));
        Log.v(TAG,"http.conn-manager.timeout: "+ params.getParameter("http.conn-manager.timeout"));
        Log.v(TAG,"http.socket.timeout: "+ params.getParameter("http.socket.timeout"));
        Log.v(TAG,"http.connection.timeout: "+ params.getParameter("http.connection.timeout"));
        
        return httpClient;
    }
    
    
    public AndroidHttpClient getHttpClient() {
        if (httpClient == null)
            httpClient = createHttpClient();
        return httpClient;
    }
    
    
    private void shutdownHttpClient() {
        if (httpClient != null) {
            if (httpClient.getConnectionManager() != null) {
                httpClient.getConnectionManager().shutdown();
            }
            
            httpClient.close();
            httpClient = null;
        }
    }

    
}
