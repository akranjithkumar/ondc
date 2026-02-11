package com.example.ondc.adapter;

import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ondc.R;
import com.example.ondc.databinding.ItemOrderBinding;
import com.example.ondc.model.Order;
import com.example.ondc.model.OrderItem;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orders;
    private final OrderActionListener listener;

    public interface OrderActionListener {
        void onAcceptOrder(Order order, int position);
        void onRejectOrder(Order order, int position);
    }

    public OrderAdapter(List<Order> orders, OrderActionListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    public void updateOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderBinding binding = ItemOrderBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(orders.get(position), position);
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private final ItemOrderBinding binding;

        OrderViewHolder(ItemOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Order order, int position) {
            // Order ID
            String orderId = order.getOndcOrderId() != null ? order.getOndcOrderId() : "#" + order.getId();
            binding.tvOrderId.setText(orderId);

            // Status chip
            binding.tvStatus.setText(order.getDisplayStatus());
            setStatusChipColor(binding.tvStatus, order.getStatus());

            // Priority
            String priority = order.getPriority();
            binding.tvPriority.setText(priority != null ? priority : "NORMAL");
            setPriorityColor(binding.tvPriority, binding.priorityIndicator, priority);

            // Customer
            binding.tvCustomer.setText(order.getCustomerName() != null ? order.getCustomerName() : "Unknown");

            // Delivery
            String delivery = order.getDeliveryAddress();
            if (delivery == null) delivery = "";
            if (order.getDeliveryPincode() != null) delivery += " - " + order.getDeliveryPincode();
            binding.tvDelivery.setText(delivery);

            // Items
            binding.tvItems.setText(order.getItemsSummary());

            // Amount
            Double amount = order.getTotalAmount();
            binding.tvAmount.setText(amount != null ? String.format("₹%.2f", amount) : "₹0.00");

            // Seller App
            binding.tvSellerApp.setText(order.getSellerAppName() != null ? "via " + order.getSellerAppName() : "");

            // Outlet
            binding.tvOutlet.setText(order.getOutletName() != null ? order.getOutletName() : "");

            // Action buttons (only show for PENDING orders)
            boolean isPending = "PENDING".equalsIgnoreCase(order.getStatus());
            binding.actionButtons.setVisibility(isPending ? View.VISIBLE : View.GONE);

            binding.btnAccept.setOnClickListener(v -> {
                if (listener != null) listener.onAcceptOrder(order, position);
            });

            binding.btnReject.setOnClickListener(v -> {
                if (listener != null) listener.onRejectOrder(order, position);
            });
        }

        private void setStatusChipColor(View chipView, String status) {
            int color;
            if (status == null) status = "";
            switch (status.toUpperCase()) {
                case "ACCEPTED":
                    color = chipView.getContext().getColor(R.color.status_accepted);
                    break;
                case "REJECTED":
                case "CANCELLED":
                    color = chipView.getContext().getColor(R.color.status_rejected);
                    break;
                case "FULFILLED":
                    color = chipView.getContext().getColor(R.color.status_fulfilled);
                    break;
                case "PARTIALLY_FULFILLED":
                    color = chipView.getContext().getColor(R.color.status_partial);
                    break;
                case "PENDING":
                default:
                    color = chipView.getContext().getColor(R.color.status_pending);
                    break;
            }

            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.RECTANGLE);
            bg.setCornerRadius(50 * chipView.getContext().getResources().getDisplayMetrics().density);
            bg.setColor(color);
            chipView.setBackground(bg);
        }

        private void setPriorityColor(View priorityText, View indicator, String priority) {
            int color;
            if (priority == null) priority = "";
            switch (priority.toUpperCase()) {
                case "HIGH":
                case "URGENT":
                    color = priorityText.getContext().getColor(R.color.priority_high);
                    break;
                case "MEDIUM":
                    color = priorityText.getContext().getColor(R.color.priority_medium);
                    break;
                case "LOW":
                default:
                    color = priorityText.getContext().getColor(R.color.priority_low);
                    break;
            }

            indicator.setBackgroundColor(color);

            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.RECTANGLE);
            bg.setCornerRadius(8 * priorityText.getContext().getResources().getDisplayMetrics().density);
            bg.setColor(color);
            priorityText.setBackground(bg);
        }
    }
}
