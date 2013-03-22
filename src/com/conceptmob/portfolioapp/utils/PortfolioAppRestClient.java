package com.conceptmob.portfolioapp.utils;

import com.loopj.android.http.*;

public class PortfolioAppRestClient {
    private static final String BASE_URL = "http://portfolioapp.conceptmob.com/api/v2/";
    
    private static AsyncHttpClient client  = new AsyncHttpClient();
    
    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }
    
    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);        
    }
    
    public static void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.put(getAbsoluteUrl(url),  params, responseHandler);
    }
    
    public static void delete(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.delete(getAbsoluteUrl(url), responseHandler);
    }
    
    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;        
    }
}
