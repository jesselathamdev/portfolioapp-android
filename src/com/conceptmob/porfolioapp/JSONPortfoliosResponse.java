package com.conceptmob.porfolioapp;

import java.util.List;

import com.google.api.client.util.Key;

public class JSONPortfoliosResponse {
    

    // ----------------------------------------------------------------------
    // Portfolios
    // ----------------------------------------------------------------------
    
    public static class Portfolios {
        @Key("response")
        public Response response;      
    }
    
    public static class Response {
        @Key("meta")
        public Meta meta;
        
        @Key("portfolios")
        public List<Portfolio> portfolios; 
    } 
    
    public static class Meta {
        @Key("request_id")
        public String requestId;
        
        @Key("status_code")
        public int statusCode;
    }
    
    public static class Portfolio {
        @Key("id")
        public int id;
        
        @Key("name")
        public String name;
    }
}
