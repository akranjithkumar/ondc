package com.example.ondc_buyer.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.Response;

public class NetworkUtils {

    /**
     * Check network connectivity using modern NetworkCapabilities API.
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;
        
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) 
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) return false;
            
            Network activeNetwork = connectivityManager.getActiveNetwork();
            if (activeNetwork == null) return false;
            
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
            if (capabilities == null) return false;
            
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                   capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                   capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get user-friendly error message from a Throwable.
     */
    public static String getErrorMessage(Throwable t) {
        if (t instanceof SocketTimeoutException) {
            return "Connection timed out. Please try again.";
        } else if (t instanceof UnknownHostException) {
            return "Unable to reach server. Check your internet connection.";
        } else if (t instanceof ConnectException) {
            return "Failed to connect to server. Is it running?";
        } else if (t instanceof IOException) {
            return "Network error. Please check your connection.";
        } else {
            String message = t.getLocalizedMessage();
            return message != null ? "Error: " + message : "Something went wrong. Please try again.";
        }
    }

    /**
     * Parse error message from API error body.
     */
    public static String getApiErrorMessage(Response<?> response) {
        try {
            if (response != null && response.errorBody() != null) {
                String errorJson = response.errorBody().string();
                JSONObject jsonObject = new JSONObject(errorJson);
                if (jsonObject.has("message")) {
                    return jsonObject.getString("message");
                } else if (jsonObject.has("error")) {
                    return jsonObject.getString("error");
                }
            }
        } catch (Exception e) {
            // Fall through to default
        }
        int code = response != null ? response.code() : 0;
        String msg = response != null ? response.message() : "Unknown error";
        return "Error " + code + ": " + msg;
    }

    /**
     * Show error Snackbar without retry.
     */
    public static void showError(View view, String message) {
        if (view != null && message != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                    .setAction("Dismiss", v -> {})
                    .show();
        }
    }

    /**
     * Show error Snackbar with retry action.
     */
    public static void showRetryError(View view, String message, Runnable retryAction) {
        if (view != null && message != null) {
            Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Retry", v -> {
                if (retryAction != null) retryAction.run();
            });
            snackbar.show();
        }
    }
}
