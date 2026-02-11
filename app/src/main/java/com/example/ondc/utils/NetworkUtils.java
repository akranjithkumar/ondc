package com.example.ondc.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import com.example.ondc.R;

public class NetworkUtils {

    private NetworkUtils() {
        // Prevent instantiation
    }

    /**
     * Check if device has active internet connection.
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        Network network = cm.getActiveNetwork();
        if (network == null) return false;

        NetworkCapabilities caps = cm.getNetworkCapabilities(network);
        return caps != null && (
                caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        );
    }

    /**
     * Show error snackbar with optional retry action.
     */
    public static void showError(View rootView, String message, Runnable retryAction) {
        if (rootView == null) return;
        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);
        if (retryAction != null) {
            snackbar.setAction("Retry", v -> retryAction.run());
        }
        snackbar.show();
    }

    /**
     * Show error snackbar without retry.
     */
    public static void showError(View rootView, String message) {
        showError(rootView, message, null);
    }

    /**
     * Show success snackbar.
     */
    public static void showSuccess(View rootView, String message) {
        if (rootView == null) return;
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Toggle loading state visibility.
     */
    public static void setLoadingState(ProgressBar progressBar, View contentView, TextView emptyView, boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        if (contentView != null) {
            contentView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        }
        if (emptyView != null && isLoading) {
            emptyView.setVisibility(View.GONE);
        }
    }

    /**
     * Show empty state.
     */
    public static void showEmptyState(View contentView, TextView emptyView, String message) {
        if (contentView != null) {
            contentView.setVisibility(View.GONE);
        }
        if (emptyView != null) {
            emptyView.setText(message);
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Show content, hide empty state.
     */
    public static void showContent(View contentView, TextView emptyView) {
        if (contentView != null) {
            contentView.setVisibility(View.VISIBLE);
        }
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
    }
}
