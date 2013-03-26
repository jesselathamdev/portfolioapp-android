package com.conceptmob.core.communication;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.net.http.AndroidHttpClient;
import android.util.Log;


public class RestClient {
    
    
    // --------------------------------------------------------------------------
    // Private declarations
    // --------------------------------------------------------------------------
    
    private String _baseUrl;
    
    
    

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
    
    public SimpleHttpResponse get(String url) {
        SimpleHttpResponse simpleResponse = new SimpleHttpResponse();
        HttpResponse response = null;
        HttpEntity entity = null;
        Boolean success = false;
        
        // define the client and set the user agent
        AndroidHttpClient client = AndroidHttpClient.newInstance(System.getProperty("http.agent"));
        HttpGet request = new HttpGet(getAbsoluteUrl(url));
        
        // connect to and get our data from the server
        try {
            response = client.execute(request);
            entity = response.getEntity();
            success = true;
        } catch (IOException e) {                       
            e.printStackTrace();
        } finally {
            client.close();
        }
        
        // load up our simple http response instance with results from the response
        simpleResponse.setSuccess(success);        
        simpleResponse.setStatusCode(response.getStatusLine().getStatusCode());
        
        // convert response body using EntityUtils
        try {
            simpleResponse.setContent(EntityUtils.toString(entity));
            entity.consumeContent();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 
        
        return simpleResponse;
    }
    
    
    public HttpResponse get(String url, HttpParams params) {
        HttpResponse response = null;
        
        AndroidHttpClient client = AndroidHttpClient.newInstance(System.getProperty("http.agent"));
        HttpGet request = new HttpGet(getAbsoluteUrl(url));
        
        try {
            response = client.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return response;
    }
    
    
    // --------------------------------------------------------------------------
    // Private helper methods
    // --------------------------------------------------------------------------
    
    private void setCommon() {
        // client.setUserAgent(System.getProperty("http.agent"));
    }
    
    private String getAbsoluteUrl(String relativeUrl) {
        return _baseUrl + relativeUrl;        
    }
}