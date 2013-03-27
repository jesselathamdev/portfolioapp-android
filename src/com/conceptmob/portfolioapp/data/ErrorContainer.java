package com.conceptmob.portfolioapp.data;

import com.fasterxml.jackson.annotation.JsonProperty;


public class ErrorContainer {
    public ErrorContainer() { }
    
    @JsonProperty("response")
    public Response response;

    
    public static class Response {
        @JsonProperty("meta")
        public Meta meta;
    }
    
    public static class Meta {
        @JsonProperty("status_code")
        public int statusCode;
        
        @JsonProperty("message")
        public String message;
        
        @JsonProperty("request_id")
        public String requestId;
    }
}