package com.example.utspemhir;

import com.google.gson.annotations.SerializedName;

public class SetoranResponse {
    private String status;
    private String message;

    // Getters and setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

