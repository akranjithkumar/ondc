package com.example.ondc_buyer.network;

import com.example.ondc_buyer.model.Order;
import com.example.ondc_buyer.model.OrderRequest;
import com.example.ondc_buyer.model.Product;
import com.example.ondc_buyer.model.Vendor;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    // Vendor Endpoints
    @GET("api/vendors")
    Call<List<Vendor>> getAllVendors();

    @GET("api/vendors/{id}")
    Call<Vendor> getVendorById(@Path("id") Long id);

    // Product Endpoints
    @GET("api/products/vendor/{vendorId}")
    Call<List<Product>> getProductsByVendor(@Path("vendorId") Long vendorId);

    // Order Endpoints
    @GET("api/orders")
    Call<List<Order>> getAllOrders(); // For MVP, we might filter client-side or use this to simulate history

    @POST("api/orders")
    Call<Order> createOrder(@Body OrderRequest request);
}
