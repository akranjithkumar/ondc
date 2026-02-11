package com.example.ondc;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ondc.adapter.OrderAdapter;
import com.example.ondc.databinding.ActivityOrdersBinding;
import com.example.ondc.model.Order;
import com.example.ondc.network.ApiClient;
import com.example.ondc.network.ApiService;
import com.example.ondc.utils.ErrorHandler;
import com.example.ondc.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersActivity extends AppCompatActivity implements OrderAdapter.OrderActionListener {

    private ActivityOrdersBinding binding;
    private ApiService apiService;
    private OrderAdapter adapter;
    private List<Order> allOrders = new ArrayList<>();
    private String currentFilter = "ALL";
    private static final long VENDOR_ID = 2L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityOrdersBinding.inflate(getLayoutInflater());
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
        loadOrders();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new OrderAdapter(new ArrayList<>(), this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupFilterChips() {
        binding.chipAll.setOnClickListener(v -> filterOrders("ALL"));
        binding.chipPending.setOnClickListener(v -> filterOrders("PENDING"));
        binding.chipAccepted.setOnClickListener(v -> filterOrders("ACCEPTED"));
        binding.chipRejected.setOnClickListener(v -> filterOrders("REJECTED"));
        binding.chipFulfilled.setOnClickListener(v -> filterOrders("FULFILLED"));
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeColors(getColor(R.color.primary));
        binding.swipeRefresh.setOnRefreshListener(this::loadOrders);
    }

    private void loadOrders() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            NetworkUtils.showError(binding.rootLayout, getString(R.string.state_no_network), this::loadOrders);
            binding.swipeRefresh.setRefreshing(false);
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.emptyState.setVisibility(View.GONE);

        apiService.getPrioritizedOrders(VENDOR_ID).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    allOrders = response.body();
                    filterOrders(currentFilter);
                } else {
                    showEmpty();
                    NetworkUtils.showError(binding.rootLayout, ErrorHandler.parseHttpError(response), () -> loadOrders());
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                showEmpty();
                NetworkUtils.showError(binding.rootLayout, ErrorHandler.parseError(t), () -> loadOrders());
            }
        });
    }

    private void filterOrders(String status) {
        currentFilter = status;
        List<Order> filtered;

        if ("ALL".equals(status)) {
            filtered = new ArrayList<>(allOrders);
        } else {
            filtered = new ArrayList<>();
            for (Order order : allOrders) {
                if (status.equalsIgnoreCase(order.getStatus())) {
                    filtered.add(order);
                }
            }
        }

        adapter.updateOrders(filtered);

        if (filtered.isEmpty()) {
            showEmpty();
        } else {
            binding.emptyState.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showEmpty() {
        binding.emptyState.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onAcceptOrder(Order order, int position) {
        if (order.getId() == null) return;

        apiService.acceptOrder(order.getId()).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful()) {
                    NetworkUtils.showSuccess(binding.rootLayout, getString(R.string.order_accepted_success));
                    loadOrders(); // Refresh list
                } else {
                    NetworkUtils.showError(binding.rootLayout, ErrorHandler.parseHttpError(response));
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                NetworkUtils.showError(binding.rootLayout, ErrorHandler.parseError(t));
            }
        });
    }

    @Override
    public void onRejectOrder(Order order, int position) {
        showRejectDialog(order);
    }

    private void showRejectDialog(Order order) {
        EditText input = new EditText(this);
        input.setHint(R.string.reject_dialog_hint);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setMinLines(2);

        FrameLayout container = new FrameLayout(this);
        int margin = (int) (20 * getResources().getDisplayMetrics().density);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(margin, margin / 2, margin, 0);
        input.setLayoutParams(params);
        container.addView(input);

        new AlertDialog.Builder(this)
                .setTitle(R.string.reject_dialog_title)
                .setView(container)
                .setPositiveButton(R.string.reject_dialog_confirm, (dialog, which) -> {
                    String reason = input.getText().toString().trim();
                    if (reason.isEmpty()) reason = "Rejected by vendor";
                    rejectOrder(order, reason);
                })
                .setNegativeButton(R.string.reject_dialog_cancel, null)
                .show();
    }

    private void rejectOrder(Order order, String reason) {
        if (order.getId() == null) return;

        Map<String, String> body = new HashMap<>();
        body.put("reason", reason);

        apiService.rejectOrder(order.getId(), body).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful()) {
                    NetworkUtils.showSuccess(binding.rootLayout, getString(R.string.order_rejected_success));
                    loadOrders();
                } else {
                    NetworkUtils.showError(binding.rootLayout, ErrorHandler.parseHttpError(response));
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                NetworkUtils.showError(binding.rootLayout, ErrorHandler.parseError(t));
            }
        });
    }
}
