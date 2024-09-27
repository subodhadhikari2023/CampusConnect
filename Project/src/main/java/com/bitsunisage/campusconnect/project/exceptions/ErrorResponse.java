package com.bitsunisage.campusconnect.project.exceptions;

public class ErrorResponse {
    private int statusCode;
    private String errorMessage;

    public ErrorResponse() {

    }

    public ErrorResponse(int statusCode, String errorMessage) {
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }


    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "statusCode=" + statusCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

