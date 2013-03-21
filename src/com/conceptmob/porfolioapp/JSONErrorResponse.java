package com.conceptmob.porfolioapp;

import com.google.api.client.util.Key;

public class JSONErrorResponse {
    

    // ----------------------------------------------------------------------
    // Error
    // ----------------------------------------------------------------------
    
    public static class Portfolios {
        @Key("response")
        public Response response;      
    }
    
    public static class Response {
        @Key("meta")
        public Meta meta;       
    } 
    
    public static class Meta {
        @Key("request_id")
        public String requestId;
        
        @Key("status_code")
        public int statusCode;
        
        @Key("message")
        public int message;
    }
}
