package com.conceptmob.portfolioapp.data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AuthTokenContainer implements Serializable {
    public AuthTokenContainer() { }
    
    @JsonProperty("response")
    public Response response;

    
    public static class Response {
        @JsonProperty("meta")
        public Meta meta;
        
        @JsonProperty("token")
        public String token;
        
        @JsonProperty("user")
        public User user;
        
        @JsonProperty("identifier")
        public String identifier;
    }
    
    public static class Meta {
        @JsonProperty("status_code")
        public int statusCode;
        
        @JsonProperty("message")
        public String message;
        
        @JsonProperty("request_id")
        public String requestId;
    }
    
    public static class User {
        @JsonProperty("id")
        public int id;
        
        @JsonProperty("first_name")
        public String firstName;
        
        @JsonProperty("last_name")
        public String lastName;
    }
}