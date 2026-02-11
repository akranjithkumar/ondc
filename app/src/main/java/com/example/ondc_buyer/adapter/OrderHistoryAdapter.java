package com.example.ondc_buyer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ondc_buyer.R;
import com.example.ondc_buyer.model.Order;
import com.google.android.material.chip.Chip;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private List<Order> orders = new ArrayList<>();

    public void setOrders(List<Order> orders) {
        this.orders = orders != null ? orders : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        if (order == null) return;

        // Order ID
        String orderId = order.getOndcOrderId();
        if (orderId != null) {
            holder.textOrderId.setText("Order #" + orderId);
        } else {
            holder.textOrderId.setText("Order #" + (order.getId() != null ? order.getId() : "—"));
        }

        // Status Chip with color coding
        String status = order.getStatus();
        if (status != null) {
            holder.chipStatus.setText(status.toUpperCase());
            int chipColor;
            switch (status.toUpperCase()) {
                case "ACCEPTED":
                case "COMPLETED":
                    chipColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.success);
                    break;
                case "REJECTED":
                case "CANCELLED":
                    chipColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.danger);
                    break;
                case "PROCESSING":
                    chipColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.warning);
                    break;
                default:
                    chipColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.primary);
                    break;
            }
            holder.chipStatus.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(chipColor));
        } else {
            holder.chipStatus.setText("UNKNOWN");
        }

        // Vendor Name
        String vendorName = order.getVendorName();
        if (vendorName != null && !vendorName.isEmpty()) {
            holder.textVendorName.setText(vendorName);
            holder.layoutVendor.setVisibility(View.VISIBLE);
        } else {
            holder.layoutVendor.setVisibility(View.GONE);
        }

        // Order Details (item count)
        int itemCount = order.getItems() != null ? order.getItems().size() : 0;
        holder.textOrderDetails.setText(itemCount + " Item" + (itemCount != 1 ? "s" : ""));

        // Date
        try {
            String createdAt = order.getCreatedAt();
            if (createdAt != null && !createdAt.isEmpty()) {
                LocalDateTime dateTime = LocalDateTime.parse(createdAt);
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
                holder.textOrderDate.setText(dateTime.format(fmt));
            } else {
                holder.textOrderDate.setText("—");
            }
        } catch (Exception e) {
            // Fallback: show raw string or dash
            String raw = order.getCreatedAt();
            holder.textOrderDate.setText(raw != null ? raw : "—");
        }

        // Delivery Address
        try {
            String address = order.getDeliveryAddress();
            if (address != null && !address.isEmpty()) {
                holder.textDeliveryAddress.setText(address);
                holder.layoutDelivery.setVisibility(View.VISIBLE);
            } else {
                holder.layoutDelivery.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            holder.layoutDelivery.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textOrderId, textVendorName, textOrderDetails, textOrderDate, textDeliveryAddress;
        Chip chipStatus;
        LinearLayout layoutVendor, layoutDelivery;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textOrderId = itemView.findViewById(R.id.text_order_id);
            chipStatus = itemView.findViewById(R.id.chip_status);
            textVendorName = itemView.findViewById(R.id.text_vendor_name);
            textOrderDetails = itemView.findViewById(R.id.text_order_details);
            textOrderDate = itemView.findViewById(R.id.text_order_date);
            textDeliveryAddress = itemView.findViewById(R.id.text_delivery_address);
            layoutVendor = itemView.findViewById(R.id.layout_vendor);
            layoutDelivery = itemView.findViewById(R.id.layout_delivery);
        }
    }
}
