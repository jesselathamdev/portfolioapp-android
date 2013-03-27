    package com.conceptmob.core.communication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
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
    
    
    public SimpleHttpResponse post(String url, List<NameValuePair> params) {
        SimpleHttpResponse simpleResponse = new SimpleHttpResponse();
        HttpResponse response = null;
        HttpEntity entity = null;
        Boolean success = false;
        
        // define the client and set the user agent
        AndroidHttpClient client = AndroidHttpClient.newInstance(System.getProperty("http.agent"));
        HttpPost request = new HttpPost(getAbsoluteUrl(url));
        
        // add in the parameters
        try {
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {            
            e.printStackTrace();
        }
                
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
        
        Log.i("PortfolioAppAPI", "Success: " + simpleResponse.getSuccess() + " | Status Code: " + simpleResponse.getStatusCode() + " | Content: " + simpleResponse.getContent());
        return simpleResponse;
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