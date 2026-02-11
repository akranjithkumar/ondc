package com.example.ondc;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ondc.databinding.ActivityMainBinding;
import com.example.ondc.model.DashboardSummary;
import com.example.ondc.model.InventoryItem;
import com.example.ondc.model.SellerApp;
import com.example.ondc.network.ApiClient;
import com.example.ondc.network.ApiService;
import com.example.ondc.utils.ErrorHandler;
import com.example.ondc.utils.NetworkUtils;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ApiService apiService;
    private static final long VENDOR_ID = 2L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiService = ApiClient.getApiService();
        setupBottomNavigation();
        setupSwipeRefresh();
        setupClickListeners();
        loadDashboard();
        loadSellerApps();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Re-select dashboard tab when returning
        binding.bottomNav.setSelectedItemId(R.id.nav_dashboard);
    }

    private void setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                return true;
            } else if (itemId == R.id.nav_orders) {
                startActivity(new Intent(this, OrdersActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            } else if (itemId == R.id.nav_inventory) {
                startActivity(new Intent(this, InventoryActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }
            return false;
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeColors(getColor(R.color.primary));
        binding.swipeRefresh.setOnRefreshListener(() -> {
            loadDashboard();
            loadSellerApps();
        });
    }

    private void setupClickListeners() {
        binding.btnViewOrders.setOnClickListener(v -> {
            startActivity(new Intent(this, OrdersActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        binding.btnSyncInventory.setOnClickListener(v -> syncInventory());

        binding.btnViewAllInventory.setOnClickListener(v -> {
            startActivity(new Intent(this, InventoryActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private void loadDashboard() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            NetworkUtils.showError(binding.main, getString(R.string.state_no_network), this::loadDashboard);
            binding.swipeRefresh.setRefreshing(false);
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        apiService.getDashboardSummary(VENDOR_ID).enqueue(new Callback<DashboardSummary>() {
            @Override
            public void onResponse(Call<DashboardSummary> call, Response<DashboardSummary> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    populateDashboard(response.body());
                } else {
                    NetworkUtils.showError(binding.main, ErrorHandler.parseHttpError(response), () -> loadDashboard());
                }
            }

            @Override
            public void onFailure(Call<DashboardSummary> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                NetworkUtils.showError(binding.main, ErrorHandler.parseError(t), () -> loadDashboard());
            }
        });
    }

    private void populateDashboard(DashboardSummary data) {
        // Vendor name
        String vendorName = data.getVendorName() != null ? data.getVendorName() : "Vendor Hub";
        binding.tvVendorName.setText(vendorName);

        // Stat cards
        binding.tvTotalOrders.setText(safeString(data.getTotalOrders()));
        binding.tvFulfillmentRate.setText(data.getFormattedFulfillmentRate());
        binding.tvPendingOrders.setText(safeString(data.getPendingOrders()));
        binding.tvActiveOutlets.setText(data.getActiveOutlets() + "/" + data.getTotalOutlets());
        binding.tvRating.setText(data.getFormattedRating());

        // Seller app count
        binding.tvSellerAppCount.setText(
                safeInt(data.getHealthySellerApps()) + " / " + safeInt(data.getTotalSellerApps()) + " healthy"
        );

        // Low stock alerts
        populateLowStockAlerts(data.getLowStockAlerts());
    }

    private void populateLowStockAlerts(List<InventoryItem> alerts) {
        binding.lowStockContainer.removeAllViews();

        if (alerts == null || alerts.isEmpty()) {
            binding.tvNoAlerts.setVisibility(View.VISIBLE);
            return;
        }

        binding.tvNoAlerts.setVisibility(View.GONE);

        // Show up to 5 alerts
        int limit = Math.min(alerts.size(), 5);
        for (int i = 0; i < limit; i++) {
            InventoryItem item = alerts.get(i);
            View card = createLowStockCard(item);
            binding.lowStockContainer.addView(card);
        }
    }

    private View createLowStockCard(InventoryItem item) {
        MaterialCardView cardView = new MaterialCardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.bottomMargin = (int) (8 * getResources().getDisplayMetrics().density);
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(2 * getResources().getDisplayMetrics().density);
        cardView.setRadius(12 * getResources().getDisplayMetrics().density);
        cardView.setCardBackgroundColor(getColor(R.color.surface));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.HORIZONTAL);
        content.setGravity(android.view.Gravity.CENTER_VERTICAL);
        int padding = (int) (12 * getResources().getDisplayMetrics().density);
        content.setPadding(padding, padding, padding, padding);

        // Warning icon
        android.widget.ImageView icon = new android.widget.ImageView(this);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                (int) (24 * getResources().getDisplayMetrics().density),
                (int) (24 * getResources().getDisplayMetrics().density)
        );
        iconParams.setMarginEnd((int) (12 * getResources().getDisplayMetrics().density));
        icon.setLayoutParams(iconParams);
        icon.setImageResource(R.drawable.ic_warning);
        icon.setColorFilter(getColor(R.color.warning));
        content.addView(icon);

        // Text container
        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView name = new TextView(this);
        name.setText(item.getProductName() != null ? item.getProductName() : "Unknown");
        name.setTextSize(13);
        name.setTextColor(getColor(R.color.text_primary));
        name.setTypeface(null, android.graphics.Typeface.BOLD);
        textContainer.addView(name);

        TextView detail = new TextView(this);
        String outlet = item.getOutletName() != null ? item.getOutletName() : "";
        detail.setText("Available: " + safeInt(item.getAvailableStock()) + " | " + outlet);
        detail.setTextSize(11);
        detail.setTextColor(getColor(R.color.text_hint));
        textContainer.addView(detail);

        content.addView(textContainer);

        // Stock count
        TextView stockCount = new TextView(this);
        stockCount.setText(safeInt(item.getAvailableStock()) + "/" + safeInt(item.getTotalStock()));
        stockCount.setTextSize(14);
        stockCount.setTextColor(getColor(R.color.error));
        stockCount.setTypeface(null, android.graphics.Typeface.BOLD);
        content.addView(stockCount);

        cardView.addView(content);
        return cardView;
    }

    private void loadSellerApps() {
        apiService.getSellerApps().enqueue(new Callback<List<SellerApp>>() {
            @Override
            public void onResponse(Call<List<SellerApp>> call, Response<List<SellerApp>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    populateSellerApps(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<SellerApp>> call, Throwable t) {
                // Silently fail for seller apps section — dashboard still shows
            }
        });
    }

    private void populateSellerApps(List<SellerApp> apps) {
        binding.sellerAppsContainer.removeAllViews();

        for (SellerApp app : apps) {
            View card = createSellerAppCard(app);
            binding.sellerAppsContainer.addView(card);
        }
    }

    private View createSellerAppCard(SellerApp app) {
        float density = getResources().getDisplayMetrics().density;

        MaterialCardView cardView = new MaterialCardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.bottomMargin = (int) (8 * density);
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(1 * density);
        cardView.setRadius(12 * density);
        cardView.setCardBackgroundColor(getColor(R.color.surface));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.HORIZONTAL);
        content.setGravity(android.view.Gravity.CENTER_VERTICAL);
        int padding = (int) (14 * density);
        content.setPadding(padding, padding, padding, padding);

        // Status dot
        View statusDot = new View(this);
        int dotSize = (int) (10 * density);
        LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(dotSize, dotSize);
        dotParams.setMarginEnd((int) (12 * density));
        statusDot.setLayoutParams(dotParams);
        statusDot.setBackgroundResource(R.drawable.bg_status_chip);
        if (app.isHealthy()) {
            statusDot.getBackground().setTint(getColor(R.color.success));
        } else {
            statusDot.getBackground().setTint(getColor(R.color.error));
        }
        content.addView(statusDot);

        // Name and status
        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView name = new TextView(this);
        name.setText(app.getName() != null ? app.getName() : "Seller App");
        name.setTextSize(14);
        name.setTextColor(getColor(R.color.text_primary));
        name.setTypeface(null, android.graphics.Typeface.BOLD);
        textContainer.addView(name);

        TextView status = new TextView(this);
        status.setText(app.getDisplayStatus());
        status.setTextSize(11);
        status.setTextColor(app.isHealthy() ? getColor(R.color.success) : getColor(R.color.error));
        textContainer.addView(status);

        content.addView(textContainer);

        // Response time
        LinearLayout metricsContainer = new LinearLayout(this);
        metricsContainer.setOrientation(LinearLayout.VERTICAL);
        metricsContainer.setGravity(android.view.Gravity.END);

        TextView responseTime = new TextView(this);
        Long rtMs = app.getResponseTimeMs();
        responseTime.setText(rtMs != null ? rtMs + "ms" : "N/A");
        responseTime.setTextSize(13);
        responseTime.setTextColor(getColor(R.color.text_primary));
        responseTime.setTypeface(null, android.graphics.Typeface.BOLD);
        metricsContainer.addView(responseTime);

        TextView uptime = new TextView(this);
        Double up = app.getUptimePercentage();
        uptime.setText(up != null ? String.format("%.1f%% uptime", up) : "");
        uptime.setTextSize(10);
        uptime.setTextColor(getColor(R.color.text_hint));
        metricsContainer.addView(uptime);

        content.addView(metricsContainer);

        cardView.addView(content);
        return cardView;
    }

    private void syncInventory() {
        binding.btnSyncInventory.setEnabled(false);

        apiService.syncInventory(VENDOR_ID).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                binding.btnSyncInventory.setEnabled(true);
                if (response.isSuccessful()) {
                    NetworkUtils.showSuccess(binding.main, getString(R.string.stock_synced));
                    loadDashboard();
                } else {
                    NetworkUtils.showError(binding.main, ErrorHandler.parseHttpError(response));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                binding.btnSyncInventory.setEnabled(true);
                NetworkUtils.showError(binding.main, ErrorHandler.parseError(t));
            }
        });
    }

    private String safeString(Object val) {
        return val != null ? String.valueOf(val) : "0";
    }

    private int safeInt(Number val) {
        return val != null ? val.intValue() : 0;
    }
}