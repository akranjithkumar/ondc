package com.example.ondc;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ondc.adapter.InventoryAdapter;
import com.example.ondc.databinding.ActivityInventoryBinding;
import com.example.ondc.model.InventoryItem;
import com.example.ondc.network.ApiClient;
import com.example.ondc.network.ApiService;
import com.example.ondc.utils.ErrorHandler;
import com.example.ondc.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryActivity extends AppCompatActivity {

    private ActivityInventoryBinding binding;
    private ApiService apiService;
    private InventoryAdapter adapter;
    private boolean showLowStockOnly = false;
    private static final long VENDOR_ID = 2L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityInventoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiService = ApiClient.getApiService();
        setupToolbar();
        setupRecyclerView();
        setupFilterChips();
        setupSwipeRefresh();
        setupSyncButton();
        loadInventory();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new InventoryAdapter(new ArrayList<>());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupFilterChips() {
        binding.chipAllStock.setOnClickListener(v -> {
            showLowStockOnly = false;
            loadInventory();
        });
        binding.chipLowStock.setOnClickListener(v -> {
            showLowStockOnly = true;
            loadLowStock();
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeColors(getColor(R.color.primary));
        binding.swipeRefresh.setOnRefreshListener(() -> {
            if (showLowStockOnly) {
                loadLowStock();
            } else {
                loadInventory();
            }
        });
    }

    private void setupSyncButton() {
        binding.btnSync.setOnClickListener(v -> syncInventory());
    }

    private void loadInventory() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            NetworkUtils.showError(binding.rootLayout, getString(R.string.state_no_network), this::loadInventory);
            binding.swipeRefresh.setRefreshing(false);
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.emptyState.setVisibility(View.GONE);

        apiService.getInventoryByVendor(VENDOR_ID).enqueue(new Callback<List<InventoryItem>>() {
            @Override
            public void onResponse(Call<List<InventoryItem>> call, Response<List<InventoryItem>> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    updateList(response.body());
                } else {
                    showEmpty();
                    NetworkUtils.showError(binding.rootLayout, ErrorHandler.parseHttpError(response), () -> loadInventory());
                }
            }

            @Override
            public void onFailure(Call<List<InventoryItem>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                showEmpty();
                NetworkUtils.showError(binding.rootLayout, ErrorHandler.parseError(t), () -> loadInventory());
            }
        });
    }

    private void loadLowStock() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            NetworkUtils.showError(binding.rootLayout, getString(R.string.state_no_network), this::loadLowStock);
            binding.swipeRefresh.setRefreshing(false);
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.emptyState.setVisibility(View.GONE);

        apiService.getLowStockByVendor(VENDOR_ID).enqueue(new Callback<List<InventoryItem>>() {
            @Override
            public void onResponse(Call<List<InventoryItem>> call, Response<List<InventoryItem>> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    updateList(response.body());
                } else {
                    showEmpty();
                    NetworkUtils.showError(binding.rootLayout, ErrorHandler.parseHttpError(response));
                }
            }

            @Override
            public void onFailure(Call<List<InventoryItem>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                showEmpty();
                NetworkUtils.showError(binding.rootLayout, ErrorHandler.parseError(t));
            }
        });
    }

    private void updateList(List<InventoryItem> items) {
        if (items.isEmpty()) {
            showEmpty();
        } else {
            binding.emptyState.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
            adapter.updateItems(items);
        }
    }

    private void showEmpty() {
        binding.emptyState.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);
        binding.tvEmpty.setText(showLowStockOnly ? getString(R.string.state_empty_alerts) : getString(R.string.state_empty_inventory));
    }

    private void syncInventory() {
        binding.btnSync.setEnabled(false);

        apiService.syncInventory(VENDOR_ID).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                binding.btnSync.setEnabled(true);
                if (response.isSuccessful()) {
                    NetworkUtils.showSuccess(binding.rootLayout, getString(R.string.stock_synced));
                    if (showLowStockOnly) {
                        loadLowStock();
                    } else {
                        loadInventory();
                    }
                } else {
                    NetworkUtils.showError(binding.rootLayout, ErrorHandler.parseHttpError(response));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                binding.btnSync.setEnabled(true);
                NetworkUtils.showError(binding.rootLayout, ErrorHandler.parseError(t));
            }
        });
    }
}
