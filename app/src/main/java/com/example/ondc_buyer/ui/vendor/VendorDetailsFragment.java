package com.example.ondc_buyer.ui.vendor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ondc_buyer.R;
import com.example.ondc_buyer.adapter.ProductAdapter;
import com.example.ondc_buyer.databinding.FragmentVendorDetailsBinding;
import com.example.ondc_buyer.model.Product;
import com.example.ondc_buyer.model.Vendor;
import com.example.ondc_buyer.network.ApiClient;
import com.example.ondc_buyer.utils.CartManager;
import com.example.ondc_buyer.utils.NetworkUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VendorDetailsFragment extends Fragment {

    private FragmentVendorDetailsBinding binding;
    private ProductAdapter productAdapter;
    private Vendor vendor;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVendorDetailsBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            try {
                vendor = (Vendor) getArguments().getSerializable("vendor");
            } catch (Exception e) {
                vendor = null;
            }
        }

        setupUI();
        setupRetryButton();
        loadProducts();

        return binding.getRoot();
    }

    private void setupUI() {
        if (binding == null) return;

        if (vendor != null) {
            binding.textVendorName.setText(vendor.getName() != null ? vendor.getName() : "Store");
            binding.textVendorAddress.setText(vendor.getAddress() != null ? vendor.getAddress() : "");

            // Rating chip
            Double rating = vendor.getRating();
            if (rating != null) {
                binding.chipRating.setText(String.format("%.1f ★", rating));
            } else {
                binding.chipRating.setText("N/A");
            }

            // Phone
            if (vendor.getContactPhone() != null && !vendor.getContactPhone().isEmpty()) {
                binding.textVendorPhone.setText(vendor.getContactPhone());
                binding.layoutPhone.setVisibility(View.VISIBLE);
            } else {
                binding.layoutPhone.setVisibility(View.GONE);
            }
        }

        binding.recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        productAdapter = new ProductAdapter((product, newQuantity) -> {
            if (vendor == null) return;
            try {
                boolean success = CartManager.getInstance().setQuantity(
                        vendor.getId(), vendor.getName(), product, newQuantity);
                if (!success) {
                    Toast.makeText(getContext(),
                            "Cannot mix items from different vendors. Clear cart first.",
                            Toast.LENGTH_SHORT).show();
                    productAdapter.notifyDataSetChanged();
                } else {
                    if (newQuantity == 0) productAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error updating cart", Toast.LENGTH_SHORT).show();
            }
        });
        binding.recyclerViewProducts.setAdapter(productAdapter);
    }

    private void setupRetryButton() {
        View errorState = binding.getRoot().findViewById(R.id.layout_error);
        if (errorState != null) {
            View retryBtn = errorState.findViewById(R.id.btn_retry);
            if (retryBtn != null) {
                retryBtn.setOnClickListener(v -> loadProducts());
            }
        }
    }

    private void loadProducts() {
        if (binding == null || vendor == null) return;

        // Check network
        if (getContext() != null && !NetworkUtils.isNetworkAvailable(getContext())) {
            showErrorState(getString(R.string.error_no_internet), getString(R.string.error_no_internet_subtitle));
            return;
        }

        showLoading();

        ApiClient.getService().getProductsByVendor(vendor.getId()).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {
                if (binding == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();
                    if (products.isEmpty()) {
                        showEmptyState();
                    } else {
                        showContent();
                        productAdapter.setProducts(products);
                        animateRecyclerView();
                    }
                } else {
                    showErrorState(getString(R.string.error_server), NetworkUtils.getApiErrorMessage(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {
                if (binding == null) return;
                showErrorState(getString(R.string.error_generic), NetworkUtils.getErrorMessage(t));
            }
        });
    }

    private void showLoading() {
        if (binding == null) return;
        binding.loadingProgress.setVisibility(View.VISIBLE);
        binding.recyclerViewProducts.setVisibility(View.GONE);
        binding.errorState.getRoot().setVisibility(View.GONE);
        binding.emptyState.getRoot().setVisibility(View.GONE);
    }

    private void showContent() {
        if (binding == null) return;
        binding.loadingProgress.setVisibility(View.GONE);
        binding.recyclerViewProducts.setVisibility(View.VISIBLE);
        binding.errorState.getRoot().setVisibility(View.GONE);
        binding.emptyState.getRoot().setVisibility(View.GONE);
    }

    private void showErrorState(String title, String subtitle) {
        if (binding == null) return;
        binding.loadingProgress.setVisibility(View.GONE);
        binding.recyclerViewProducts.setVisibility(View.GONE);
        binding.emptyState.getRoot().setVisibility(View.GONE);

        View errorState = binding.errorState.getRoot();
        errorState.setVisibility(View.VISIBLE);

        android.widget.TextView titleView = errorState.findViewById(R.id.text_error_title);
        android.widget.TextView subtitleView = errorState.findViewById(R.id.text_error_subtitle);
        if (titleView != null) titleView.setText(title);
        if (subtitleView != null) subtitleView.setText(subtitle);
    }

    private void showEmptyState() {
        if (binding == null) return;
        binding.loadingProgress.setVisibility(View.GONE);
        binding.recyclerViewProducts.setVisibility(View.GONE);
        binding.errorState.getRoot().setVisibility(View.GONE);

        View emptyState = binding.emptyState.getRoot();
        emptyState.setVisibility(View.VISIBLE);

        android.widget.ImageView icon = emptyState.findViewById(R.id.image_empty);
        android.widget.TextView titleView = emptyState.findViewById(R.id.text_empty_title);
        android.widget.TextView subtitleView = emptyState.findViewById(R.id.text_empty_subtitle);
        if (icon != null) icon.setImageResource(R.drawable.ic_store);
        if (titleView != null) titleView.setText(R.string.msg_no_products);
        if (subtitleView != null) subtitleView.setText(R.string.msg_no_products_subtitle);
    }

    private void animateRecyclerView() {
        if (binding == null || binding.recyclerViewProducts == null) return;
        try {
            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(
                    getContext(), android.R.anim.slide_in_left);
            binding.recyclerViewProducts.setLayoutAnimation(animation);
            binding.recyclerViewProducts.scheduleLayoutAnimation();
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
