package com.example.ondc_buyer.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ondc_buyer.R;
import com.example.ondc_buyer.adapter.VendorAdapter;
import com.example.ondc_buyer.databinding.FragmentHomeBinding;
import com.example.ondc_buyer.model.Vendor;
import com.example.ondc_buyer.network.ApiClient;
import com.example.ondc_buyer.utils.NetworkUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private VendorAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        setupRecyclerView();
        setupRetryButton();
        loadVendors();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.recyclerViewVendors.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new VendorAdapter(this::onVendorClick);
        binding.recyclerViewVendors.setAdapter(adapter);
    }

    private void setupRetryButton() {
        View errorState = binding.getRoot().findViewById(R.id.layout_error);
        if (errorState != null) {
            View retryBtn = errorState.findViewById(R.id.btn_retry);
            if (retryBtn != null) {
                retryBtn.setOnClickListener(v -> loadVendors());
            }
        }
    }

    private void loadVendors() {
        if (binding == null) return;

        // Check network first
        if (getContext() != null && !NetworkUtils.isNetworkAvailable(getContext())) {
            showErrorState(getString(R.string.error_no_internet), getString(R.string.error_no_internet_subtitle));
            return;
        }

        showLoading();

        ApiClient.getService().getAllVendors().enqueue(new Callback<List<Vendor>>() {
            @Override
            public void onResponse(@NonNull Call<List<Vendor>> call, @NonNull Response<List<Vendor>> response) {
                if (binding == null) return; // Fragment might be destroyed

                if (response.isSuccessful() && response.body() != null) {
                    List<Vendor> vendors = response.body();
                    if (vendors.isEmpty()) {
                        showEmptyState();
                    } else {
                        showContent();
                        adapter.setVendors(vendors);
                        animateRecyclerView();
                    }
                } else {
                    showErrorState(getString(R.string.error_server), NetworkUtils.getApiErrorMessage(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Vendor>> call, @NonNull Throwable t) {
                if (binding == null) return;
                showErrorState(getString(R.string.error_generic), NetworkUtils.getErrorMessage(t));
            }
        });
    }

    private void showLoading() {
        if (binding == null) return;
        binding.loadingProgress.setVisibility(View.VISIBLE);
        binding.recyclerViewVendors.setVisibility(View.GONE);
        binding.errorState.getRoot().setVisibility(View.GONE);
        binding.emptyState.getRoot().setVisibility(View.GONE);
    }

    private void showContent() {
        if (binding == null) return;
        binding.loadingProgress.setVisibility(View.GONE);
        binding.recyclerViewVendors.setVisibility(View.VISIBLE);
        binding.errorState.getRoot().setVisibility(View.GONE);
        binding.emptyState.getRoot().setVisibility(View.GONE);
    }

    private void showErrorState(String title, String subtitle) {
        if (binding == null) return;
        binding.loadingProgress.setVisibility(View.GONE);
        binding.recyclerViewVendors.setVisibility(View.GONE);
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
        binding.recyclerViewVendors.setVisibility(View.GONE);
        binding.errorState.getRoot().setVisibility(View.GONE);

        View emptyState = binding.emptyState.getRoot();
        emptyState.setVisibility(View.VISIBLE);

        android.widget.ImageView icon = emptyState.findViewById(R.id.image_empty);
        android.widget.TextView titleView = emptyState.findViewById(R.id.text_empty_title);
        android.widget.TextView subtitleView = emptyState.findViewById(R.id.text_empty_subtitle);
        if (icon != null) icon.setImageResource(R.drawable.ic_store);
        if (titleView != null) titleView.setText(R.string.msg_no_vendors);
        if (subtitleView != null) subtitleView.setText(R.string.msg_no_vendors_subtitle);
    }

    private void animateRecyclerView() {
        if (binding == null || binding.recyclerViewVendors == null) return;
        try {
            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(
                    getContext(), android.R.anim.slide_in_left);
            binding.recyclerViewVendors.setLayoutAnimation(animation);
            binding.recyclerViewVendors.scheduleLayoutAnimation();
        } catch (Exception e) {
            // Skip animation if it fails
        }
    }

    private void onVendorClick(Vendor vendor) {
        try {
            Bundle bundle = new Bundle();
            bundle.putSerializable("vendor", vendor);
            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.navigation_vendor_details, bundle);
        } catch (Exception e) {
            if (binding != null) {
                NetworkUtils.showError(binding.getRoot(), "Unable to open vendor details");
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
