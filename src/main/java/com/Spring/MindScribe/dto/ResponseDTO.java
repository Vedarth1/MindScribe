package com.Spring.MindScribe.dto;

public class ResponseDTO {

    private String token;
    private String message;

    public ResponseDTO() {}

    public ResponseDTO(String token, String message) {
        this.token = token;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
