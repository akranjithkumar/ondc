package com.example.ondc_buyer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ondc_buyer.R;
import com.example.ondc_buyer.model.Product;
import com.google.android.material.button.MaterialButton;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface OnRemoveListener {
        void onRemove(Product product);
    }

    private List<Map.Entry<Product, Integer>> items = new ArrayList<>();
    private final OnRemoveListener listener;

    public CartAdapter(OnRemoveListener listener) {
        this.listener = listener;
    }

    public void setItems(Map<Product, Integer> cartItems) {
        this.items = new ArrayList<>();
        if (cartItems != null) {
            for (Map.Entry<Product, Integer> entry : cartItems.entrySet()) {
                this.items.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()));
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Map.Entry<Product, Integer> entry = items.get(position);
        if (entry == null) return;

        Product product = entry.getKey();
        int quantity = entry.getValue() != null ? entry.getValue() : 1;

        if (product == null) return;

        holder.textProductName.setText(product.getName() != null ? product.getName() : "Product");
        holder.textQuantity.setText("Qty: " + quantity);

        try {
            double unitPrice = product.getPrice();
            double totalPrice = unitPrice * quantity;
            holder.textItemPrice.setText(String.format(Locale.getDefault(), "₹%.2f", totalPrice));
            holder.textUnitPrice.setText(String.format(Locale.getDefault(), "@ ₹%.2f each", unitPrice));
            holder.textUnitPrice.setVisibility(quantity > 1 ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            holder.textItemPrice.setText("₹0.00");
            holder.textUnitPrice.setVisibility(View.GONE);
        }

        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) listener.onRemove(product);
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView textProductName, textQuantity, textItemPrice, textUnitPrice;
        MaterialButton btnRemove;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textQuantity = itemView.findViewById(R.id.text_quantity);
            textItemPrice = itemView.findViewById(R.id.text_item_price);
            textUnitPrice = itemView.findViewById(R.id.text_unit_price);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }
}
