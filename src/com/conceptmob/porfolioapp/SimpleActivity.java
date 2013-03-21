package com.conceptmob.porfolioapp;

import java.io.IOException;
import java.util.List;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.Key;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;


public class SimpleActivity extends Activity {

    static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static final JsonFactory JSON_FACTORY = new JacksonFactory();  
  
    private TextView tvMessage;
    private String message = "";
  
    @Override
    public void onCreate(Bundle savedInstanceState) {
      
        StrictMode.ThreadPolicy policy = new StrictMode.
        ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); 
      
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
            
        Log.v("ACTIVITY", "SCREEN: Loaded Simple screen.");

        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                    
            @Override
            public void initialize(HttpRequest request) throws IOException {
                request.setParser(new JsonObjectParser(JSON_FACTORY));              
            }
            
        });
        
        PortfolioAppUrl url = new PortfolioAppUrl("http://10.0.2.2:8000/api/v2/portfolios/");
        
        tvMessage = (TextView)findViewById(R.id.tvSimpleActivityLabel01);
        
        try {
            HttpRequest request = requestFactory.buildGetRequest(url);
            Log.v("STATUS", "Before request.execute");

            request.setInterceptor(new HttpExecuteInterceptor() { 
                @Override
                public void intercept(HttpRequest request) throws IOException {
                    Log.v("INTERCEPTOR", "request: " + request.toString());                    
                }                
            });

            HttpResponse response = request.execute();
            
            Log.v("STATUS", "After request.execute");
            tvMessage.setText(response.toString());
            Log.v("STATUS", "After TextView assignment");
            
//            if (response.getStatusCode() != 200) {
//                Log.v("API Server Response", "there was an error" + response.toString());              
//            } else {
//                
//                JSONPortfoliosResponse.Portfolios portfolios = request.execute().parseAs(JSONPortfoliosResponse.Portfolios.class);
//                if (portfolios.response.portfolios.isEmpty()) { 
//                    Log.v("PARSING", "portfolios list is empty");
//                } else {
//                    Log.v("PARSING", "size" + portfolios.toString());              
//                }
//                
//                message = String.valueOf(portfolios.response.portfolios.size());
//                
//                
//                tvMessage.setText(message);
//            }
                        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static class PortfolioAppUrl extends GenericUrl {
        
        public PortfolioAppUrl(String encodedUrl) {
            super(encodedUrl);
        }
        
        @Key
        public String fields;
    }
    
    
  
}
