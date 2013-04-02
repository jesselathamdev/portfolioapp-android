package com.conceptmob.core.communication;


public class SimpleServerResponse {
    private Boolean success;
    private String content;
    private int statusCode;
    
    
    public SimpleServerResponse() {
        this.success = false;  // set as false by default
    }
    
    public void setSuccess(Boolean success) { this.success = success; }
    public Boolean getSuccess() { return this.success; }
    
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
    public int getStatusCode() { return this.statusCode; }
    
    public void setContent(String content) { this.content = content; }
    public String getContent() { return this.content; }
}
