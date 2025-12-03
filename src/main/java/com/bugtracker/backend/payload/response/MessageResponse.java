// src/main/java/com/bugtracker/backend/payload/response/MessageResponse.java
package com.bugtracker.backend.payload.response;

public class MessageResponse {
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}