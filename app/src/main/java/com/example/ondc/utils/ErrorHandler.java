package com.example.ondc.utils;

import android.view.View;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.Response;

public class ErrorHandler {

    private ErrorHandler() {
        // Prevent instantiation
    }

    /**
     * Parse a Retrofit error Throwable into a user-friendly message.
     */
    public static String parseError(Throwable t) {
        if (t == null) return "An unexpected error occurred.";

        if (t instanceof SocketTimeoutException) {
            return "Request timed out. Please check your connection and try again.";
        }
        if (t instanceof ConnectException || t instanceof UnknownHostException) {
            return "Unable to connect to server. Make sure the backend is running.";
        }
        if (t instanceof java.io.IOException) {
            return "Network error. Please check your connection.";
        }

        String message = t.getMessage();
        return message != null ? message : "An unexpected error occurred.";
    }

    /**
     * Parse HTTP response error into user-friendly message.
     */
    public static String parseHttpError(Response<?> response) {
        if (response == null) return "Empty response from server.";

        int code = response.code();
        switch (code) {
            case 400:
                return "Invalid request. Please check your input.";
            case 401:
                return "Unauthorized. Please check your credentials.";
            case 403:
                return "Forbidden. You don't have permission.";
            case 404:
                return "Resource not found.";
            case 409:
                return "Conflict. The resource may have been modified.";
            case 422:
                return "Invalid data. Please check your input.";
            case 500:
                return "Server error. Please try again later.";
            case 502:
            case 503:
                return "Service unavailable. Please try again later.";
            default:
                return "Error (" + code + "). Please try again.";
        }
    }

    /**
     * Check if the error is a network connectivity issue.
     */
    public static boolean isNetworkError(Throwable t) {
        return t instanceof ConnectException
                || t instanceof UnknownHostException
                || t instanceof SocketTimeoutException;
    }
}
