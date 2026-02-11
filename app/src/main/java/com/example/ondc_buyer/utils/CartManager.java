package com.example.ondc_buyer.utils;

import android.util.Log;

import com.example.ondc_buyer.model.OrderItemRequest;
import com.example.ondc_buyer.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartManager {
    private static CartManager instance;
    
    private Long currentVendorId = null;
    private String currentVendorName = null;
    
    // Map of Product -> Quantity
    private final Map<Product, Integer> cartItems = new HashMap<>();

    private CartManager() {}

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public boolean addItem(Long vendorId, String vendorName, Product product, int quantity) {
        if (currentVendorId != null && !currentVendorId.equals(vendorId)) {
            // Can only order from one vendor at a time
            return false;
        }
        
        currentVendorId = vendorId;
        currentVendorName = vendorName;
        
        int currentQty = cartItems.getOrDefault(product, 0);
        cartItems.put(product, currentQty + quantity);
        return true;
    }
    
    public void removeItem(Product product) {
        if (cartItems.containsKey(product)) {
            cartItems.remove(product);
            if (cartItems.isEmpty()) {
                clearCart();
            }
        }
    }
    
    // Helper to get current quantity of a product
    public int getQuantity(Product product) {
        return cartItems.getOrDefault(product, 0);
    }
    
    // Set absolute quantity (used by +/- buttons)
    public boolean setQuantity(Long vendorId, String vendorName, Product product, int quantity) {
         if (currentVendorId != null && !currentVendorId.equals(vendorId)) {
            // Can only order from one vendor at a time
            return false;
        }
        if (currentVendorId == null) {
             currentVendorId = vendorId;
             currentVendorName = vendorName;
        }

        if (quantity <= 0) {
            removeItem(product);
        } else {
            cartItems.put(product, quantity);
        }
        return true;
    }
    
    public void updateQuantity(Product product, int quantity) {
        if (quantity <= 0) {
            removeItem(product);
        } else {
            cartItems.put(product, quantity);
        }
    }

    public void clearCart() {
        cartItems.clear();
        currentVendorId = null;
        currentVendorName = null;
    }

    public Map<Product, Integer> getCartItems() {
        return cartItems;
    }

    public Long getCurrentVendorId() {
        return currentVendorId;
    }

    public String getCurrentVendorName() {
        return currentVendorName;
    }

    public double getTotalPrice() {
        double total = 0.0;
        for (Map.Entry<Product, Integer> entry : cartItems.entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
        return total;
    }
    
    public List<OrderItemRequest> getOrderItemsForApi() {
        List<OrderItemRequest> items = new ArrayList<>();
        for (Map.Entry<Product, Integer> entry : cartItems.entrySet()) {
            items.add(new OrderItemRequest(entry.getKey().getId(), entry.getValue()));
        }
        return items;
    }
}
