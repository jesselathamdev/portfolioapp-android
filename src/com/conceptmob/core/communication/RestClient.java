package com.conceptmob.core.communication;

// Uses android-async-http base on Apache Http Client (from http://loopj.com/android-async-http/)
import com.loopj.android.http.*;

public class RestClient {
    
    
    // --------------------------------------------------------------------------
    // Private declarations
    // --------------------------------------------------------------------------
    
    private String _baseUrl;
    
    private static AsyncHttpClient client  = new AsyncHttpClient();
    

    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    public RestClient() {
        setCommon();
    }
    
    public RestClient(String baseUrl) {
        setCommon();
        _baseUrl = baseUrl;
    }

    
    // --------------------------------------------------------------------------
    // Getters and Setters
    // --------------------------------------------------------------------------
    
    public void setBaseUrl(String URL) { _baseUrl = URL; }
    public String getBaseUrl() { return _baseUrl; }


    // --------------------------------------------------------------------------
    // Public methods
    // --------------------------------------------------------------------------
    
    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {         
        client.get(getAbsoluteUrl(url), params, responseHandler);        
    }
    
    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);        
    }
    
    public void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.put(getAbsoluteUrl(url),  params, responseHandler);
    }
    
    public void delete(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.delete(getAbsoluteUrl(url), responseHandler);
    }
    
    
    // --------------------------------------------------------------------------
    // Private helper methods
    // --------------------------------------------------------------------------
    
    private void setCommon() {
        client.setUserAgent(System.getProperty("http.agent"));
    }
    
    private String getAbsoluteUrl(String relativeUrl) {
        return _baseUrl + relativeUrl;        
    }
}