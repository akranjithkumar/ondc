package com.example.ondc.network;

import com.example.ondc.model.DashboardSummary;
import com.example.ondc.model.InventoryItem;
import com.example.ondc.model.Order;
import com.example.ondc.model.OrderRequest;
import com.example.ondc.model.Outlet;
import com.example.ondc.model.Product;
import com.example.ondc.model.SellerApp;
import com.example.ondc.model.Vendor;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // ─── Vendors ────────────────────────────────────────

    @GET("api/vendors")
    Call<List<Vendor>> getVendors();

    @GET("api/vendors/{id}")
    Call<Vendor> getVendor(@Path("id") long id);

    // ─── Outlets ────────────────────────────────────────

    @GET("api/outlets")
    Call<List<Outlet>> getOutlets();

    @GET("api/outlets/vendor/{vendorId}")
    Call<List<Outlet>> getOutletsByVendor(@Path("vendorId") long vendorId);

    @GET("api/outlets/optimal")
    Call<Outlet> getOptimalOutlet(
            @Query("vendorId") long vendorId,
            @Query("deliveryPincode") String deliveryPincode
    );

    // ─── Products ───────────────────────────────────────

    @GET("api/products")
    Call<List<Product>> getProducts();

    @GET("api/products/vendor/{vendorId}")
    Call<List<Product>> getProductsByVendor(@Path("vendorId") long vendorId);

    // ─── Inventory ──────────────────────────────────────

    @GET("api/inventory/outlet/{outletId}")
    Call<List<InventoryItem>> getInventoryByOutlet(@Path("outletId") long outletId);

    @GET("api/inventory/vendor/{vendorId}")
    Call<List<InventoryItem>> getInventoryByVendor(@Path("vendorId") long vendorId);

    @GET("api/inventory/check")
    Call<InventoryItem> checkStock(
            @Query("productId") long productId,
            @Query("outletId") long outletId
    );

    @POST("api/inventory/reserve")
    Call<InventoryItem> reserveStock(
            @Query("productId") long productId,
            @Query("outletId") long outletId,
            @Query("quantity") int quantity
    );

    @POST("api/inventory/release")
    Call<InventoryItem> releaseStock(
            @Query("productId") long productId,
            @Query("outletId") long outletId,
            @Query("quantity") int quantity
    );

    @GET("api/inventory/low-stock")
    Call<List<InventoryItem>> getLowStockAlerts();

    @GET("api/inventory/low-stock/vendor/{vendorId}")
    Call<List<InventoryItem>> getLowStockByVendor(@Path("vendorId") long vendorId);

    @POST("api/inventory/sync/{vendorId}")
    Call<Void> syncInventory(@Path("vendorId") long vendorId);

    // ─── Orders ─────────────────────────────────────────

    @GET("api/orders")
    Call<List<Order>> getOrders();

    @GET("api/orders/vendor/{vendorId}")
    Call<List<Order>> getOrdersByVendor(@Path("vendorId") long vendorId);

    @GET("api/orders/vendor/{vendorId}/prioritized")
    Call<List<Order>> getPrioritizedOrders(@Path("vendorId") long vendorId);

    @GET("api/orders/vendor/{vendorId}/status/{status}")
    Call<List<Order>> getOrdersByStatus(
            @Path("vendorId") long vendorId,
            @Path("status") String status
    );

    @POST("api/orders")
    Call<Order> createOrder(@Body OrderRequest orderRequest);

    @PUT("api/orders/{id}/accept")
    Call<Order> acceptOrder(@Path("id") long id);

    @PUT("api/orders/{id}/reject")
    Call<Order> rejectOrder(@Path("id") long id, @Body Map<String, String> body);

    @PUT("api/orders/{id}/partial-fulfill")
    Call<Order> partialFulfillOrder(@Path("id") long id);

    // ─── Seller Apps ────────────────────────────────────

    @GET("api/seller-apps")
    Call<List<SellerApp>> getSellerApps();

    @GET("api/seller-apps/healthy")
    Call<List<SellerApp>> getHealthySellerApps();

    @GET("api/seller-apps/{id}/health")
    Call<SellerApp> checkSellerAppHealth(@Path("id") long id);

    // ─── Dashboard ──────────────────────────────────────

    @GET("api/dashboard/summary/{vendorId}")
    Call<DashboardSummary> getDashboardSummary(@Path("vendorId") long vendorId);
}
