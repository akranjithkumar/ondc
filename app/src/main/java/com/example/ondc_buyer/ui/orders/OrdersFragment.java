package com.example.ondc_buyer.ui.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ondc_buyer.R;
import com.example.ondc_buyer.adapter.OrderHistoryAdapter;
import com.example.ondc_buyer.databinding.FragmentOrdersBinding;
import com.example.ondc_buyer.model.Order;
import com.example.ondc_buyer.network.ApiClient;
import com.example.ondc_buyer.utils.NetworkUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersFragment extends Fragment {

    private FragmentOrdersBinding binding;
    private OrderHistoryAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrdersBinding.inflate(inflater, container, false);

        setupRecyclerView();
        setupRetryButton();
        loadOrders();

        binding.swipeRefresh.setColorSchemeResources(R.color.primary);
        binding.swipeRefresh.setOnRefreshListener(this::loadOrders);

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.recyclerViewOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrderHistoryAdapter();
        binding.recyclerViewOrders.setAdapter(adapter);
    }

    private void setupRetryButton() {
        View errorState = binding.getRoot().findViewById(R.id.layout_error);
        if (errorState != null) {
            View retryBtn = errorState.findViewById(R.id.btn_retry);
            if (retryBtn != null) {
                retryBtn.setOnClickListener(v -> loadOrders());
            }
        }
    }

    private void loadOrders() {
        if (binding == null) return;

        // Check network first
        if (getContext() != null && !NetworkUtils.isNetworkAvailable(getContext())) {
            binding.swipeRefresh.setRefreshing(false);
            showErrorState(getString(R.string.error_no_internet), getString(R.string.error_no_internet_subtitle));
            return;
        }

        binding.swipeRefresh.setRefreshing(true);
        binding.errorState.getRoot().setVisibility(View.GONE);
        binding.layoutEmptyOrders.setVisibility(View.GONE);

        ApiClient.getService().getAllOrders().enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(@NonNull Call<List<Order>> call, @NonNull Response<List<Order>> response) {
                if (binding == null) return;

                binding.swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Order> allOrders = response.body();

                    if (allOrders.isEmpty()) {
                        binding.layoutEmptyOrders.setVisibility(View.VISIBLE);
                        binding.recyclerViewOrders.setVisibility(View.GONE);
                    } else {
                        binding.layoutEmptyOrders.setVisibility(View.GONE);
                        binding.recyclerViewOrders.setVisibility(View.VISIBLE);
                        adapter.setOrders(allOrders);
                        animateRecyclerView();
                    }
                } else {
                    showErrorState(getString(R.string.error_server), NetworkUtils.getApiErrorMessage(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Order>> call, @NonNull Throwable t) {
                if (binding == null) return;
                binding.swipeRefresh.setRefreshing(false);
                showErrorState(getString(R.string.error_generic), NetworkUtils.getErrorMessage(t));
            }
        });
    }

    private void showErrorState(String title, String subtitle) {
        if (binding == null) return;
        binding.recyclerViewOrders.setVisibility(View.GONE);
        binding.layoutEmptyOrders.setVisibility(View.GONE);

        View errorState = binding.errorState.getRoot();
        errorState.setVisibility(View.VISIBLE);

        android.widget.TextView titleView = errorState.findViewById(R.id.text_error_title);
        android.widget.TextView subtitleView = errorState.findViewById(R.id.text_error_subtitle);
        if (titleView != null) titleView.setText(title);
        if (subtitleView != null) subtitleView.setText(subtitle);
    }

    private void animateRecyclerView() {
        if (binding == null || binding.recyclerViewOrders == null) return;
        try {
            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(
                    getContext(), android.R.anim.slide_in_left);
            binding.recyclerViewOrders.setLayoutAnimation(animation);
            binding.recyclerViewOrders.scheduleLayoutAnimation();
        } catch (Exception e) {
            // Skip animation
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
