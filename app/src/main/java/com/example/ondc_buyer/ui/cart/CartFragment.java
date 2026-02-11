package com.example.ondc_buyer.ui.cart;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ondc_buyer.R;
import com.example.ondc_buyer.adapter.CartAdapter;
import com.example.ondc_buyer.databinding.FragmentCartBinding;
import com.example.ondc_buyer.model.Order;
import com.example.ondc_buyer.model.OrderRequest;
import com.example.ondc_buyer.model.Product;
import com.example.ondc_buyer.network.ApiClient;
import com.example.ondc_buyer.utils.CartManager;
import com.example.ondc_buyer.utils.NetworkUtils;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    private CartAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);

        setupRecyclerView();
        updateUI();

        binding.btnCheckout.setOnClickListener(v -> placeOrder());

        // Browse stores button in empty state
        binding.btnBrowse.setOnClickListener(v -> {
            try {
                Navigation.findNavController(binding.getRoot())
                        .navigate(R.id.navigation_home);
            } catch (Exception e) {
                // Ignore navigation errors
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding != null) updateUI();
    }

    private void setupRecyclerView() {
        binding.recyclerViewCart.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CartAdapter(this::removeItem);
        binding.recyclerViewCart.setAdapter(adapter);
    }

    private void updateUI() {
        if (binding == null) return;

        CartManager cart = CartManager.getInstance();
        if (cart.getCartItems().isEmpty()) {
            binding.layoutEmptyCart.setVisibility(View.VISIBLE);
            binding.recyclerViewCart.setVisibility(View.GONE);
            binding.layoutCheckout.setVisibility(View.GONE);
            binding.chipVendorName.setVisibility(View.GONE);
        } else {
            binding.layoutEmptyCart.setVisibility(View.GONE);
            binding.recyclerViewCart.setVisibility(View.VISIBLE);
            binding.layoutCheckout.setVisibility(View.VISIBLE);

            // Vendor chip
            String vendorName = cart.getCurrentVendorName();
            if (vendorName != null && !vendorName.isEmpty()) {
                binding.chipVendorName.setText(vendorName);
                binding.chipVendorName.setVisibility(View.VISIBLE);
            } else {
                binding.chipVendorName.setVisibility(View.GONE);
            }

            adapter.setItems(cart.getCartItems());
            binding.textTotalPrice.setText(String.format("₹%.2f", cart.getTotalPrice()));
        }
    }

    private void removeItem(Product product) {
        try {
            CartManager.getInstance().removeItem(product);
            updateUI();
        } catch (Exception e) {
            if (binding != null) {
                NetworkUtils.showError(binding.getRoot(), "Error removing item");
            }
        }
    }

    private void placeOrder() {
        if (binding == null) return;

        CartManager cart = CartManager.getInstance();
        if (cart.getCartItems().isEmpty()) return;

        // Check network
        if (getContext() != null && !NetworkUtils.isNetworkAvailable(getContext())) {
            NetworkUtils.showRetryError(binding.getRoot(),
                    getString(R.string.error_no_internet), this::placeOrder);
            return;
        }

        // Construct OrderRequest
        OrderRequest request = new OrderRequest();
        request.setOndcOrderId("ONDC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        request.setVendorId(cart.getCurrentVendorId());
        request.setSellerAppId(1L);
        request.setPriority("HIGH");

        // Hardcoded Customer Info (MVP)
        request.setCustomerName("Amit");
        request.setCustomerPhone("+91-9999999999");
        request.setDeliveryAddress("123 HSR Layout");
        request.setDeliveryPincode("560102");
        request.setItems(cart.getOrderItemsForApi());

        binding.btnCheckout.setEnabled(false);
        binding.btnCheckout.setText("Processing...");

        ApiClient.getService().createOrder(request).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(@NonNull Call<Order> call, @NonNull Response<Order> response) {
                if (binding == null) return;

                binding.btnCheckout.setEnabled(true);
                binding.btnCheckout.setText(R.string.action_checkout);

                if (response.isSuccessful()) {
                    cart.clearCart();
                    updateUI();
                    showSuccessDialog();
                } else {
                    NetworkUtils.showError(binding.getRoot(), NetworkUtils.getApiErrorMessage(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Order> call, @NonNull Throwable t) {
                if (binding == null) return;

                binding.btnCheckout.setEnabled(true);
                binding.btnCheckout.setText(R.string.action_checkout);
                NetworkUtils.showRetryError(binding.getRoot(),
                        NetworkUtils.getErrorMessage(t), () -> placeOrder());
            }
        });
    }

    private void showSuccessDialog() {
        if (getContext() == null) return;

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View dialogView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, null);

            builder.setTitle("🎉 Order Placed!")
                    .setMessage(getString(R.string.msg_order_placed_subtitle))
                    .setPositiveButton("View Orders", (dialog, which) -> {
                        try {
                            if (binding != null) {
                                Navigation.findNavController(binding.getRoot())
                                        .navigate(R.id.navigation_orders);
                            }
                        } catch (Exception e) {
                            // Ignore
                        }
                    })
                    .setNegativeButton("Continue Shopping", (dialog, which) -> {
                        try {
                            if (binding != null) {
                                Navigation.findNavController(binding.getRoot())
                                        .navigate(R.id.navigation_home);
                            }
                        } catch (Exception e) {
                            // Ignore
                        }
                    })
                    .setCancelable(false)
                    .show();
        } catch (Exception e) {
            // Fallback: just navigate to orders
            try {
                if (binding != null) {
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.navigation_orders);
                }
            } catch (Exception ex) {
                // Ignore
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
