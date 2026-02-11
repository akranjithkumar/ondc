package com.example.ondc_buyer;

import android.app.Application;
import android.util.Log;

public class BuyerApplication extends Application {

    private static final String TAG = "BuyerApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        // Global Uncaught Exception Handler
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Log.e(TAG, "FATAL: Uncaught exception on thread " + thread.getName(), throwable);

            // Let the default handler finish the process gracefully
            // In production, you'd send this to a crash reporting service like Firebase Crashlytics
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        });
    }
}
