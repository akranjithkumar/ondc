package com.example.ondc_buyer;

import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.ui.NavigationUI;

import com.example.ondc_buyer.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Style the status bar
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary));
        window.getDecorView().setSystemUiVisibility(0); // Light icons on dark status bar

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = binding.navView;

        // Setup Navigation safely
        try {
            androidx.fragment.app.Fragment navHostFragment = getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment_activity_main);

            if (navHostFragment != null) {
                NavController navController = androidx.navigation.fragment.NavHostFragment.findNavController(navHostFragment);
                NavigationUI.setupWithNavController(navView, navController);
            }
        } catch (Exception e) {
            // Log but don't crash
            android.util.Log.e("MainActivity", "Failed to setup navigation", e);
        }
    }
}