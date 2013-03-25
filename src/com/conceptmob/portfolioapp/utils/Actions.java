package com.conceptmob.portfolioapp.utils;

import android.util.Log;

import com.conceptmob.core.communication.RestClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class Actions {


    // --------------------------------------------------------------------------
    // Private declarations
    // --------------------------------------------------------------------------
    
    private RestClient restClient = new RestClient("http://10.0.2.2:8000/api/v2/");
    private String identifier = "9099099880";


    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------
    
    public Actions() {
        
    }
    
    
    // --------------------------------------------------------------------------
    // Public methods
    // --------------------------------------------------------------------------
    
    // Creates a new token based off of the users credentials and an application identifier
    public String getToken(String email, String password) {
        final String _response = "";
        
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("password", password);
        params.put("identifier", identifier);
       
        restClient.post("auth/token/create", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Log.i("PortfolioAppAPI", "getToken onSuccess: " + response);
                Log.i("PortfolioAppAPI", "getToken onSuccess complete");
                _response = response;
            }
            
            @Override
            public void onFailure(Throwable e, String response) {
                Log.i("PortfolioAppAPI", "getToken onFailure: " + response);
                Log.i("PortfolioAppAPI", "getToken onFailure complete");
            }
            
            @Override
            public void onFinish() {
                
                Log.i("PortfolioAppAPI", "getToken onFinish complete");                
            }
        });
        
        return "";
    }
//        restClient.get("auth/token/create", params, responseHandler)
//        .get("auth/token/create?email=user1@conceptmob.com&password=access&identifier=12345", null, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(String response) {
//                try {                    
//                    message = response;
//                    Log.v("API SUCCESS", "onSuccess: " + response.toString());
//                } catch (Exception e) {
//                    Log.v("API ERROR", "onError: " + e.toString());
//                }                
//            }
//            
//            @Override
//            public void onFailure(Throwable e, String response) {                
//                Log.v("API ERROR", "onFailure: " + response);
//                Log.v("API ERROR", "onFailure: " + e.getMessage());
//                Log.v("API ERROR", "onFailure: " + e.toString());
//                Log.v("API ERROR", "onFailure: " + e.getCause());
//                
//                message = response;                
//            }
//            
//            @Override 
//            public void onFinish() {
//                Log.v("API MESSAGE", "onFinish complete");
//                
//                tvMessage.setText(message);
//            }
        
//        return "";
//    }
    
}
