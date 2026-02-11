package com.example.ondc_buyer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ondc_buyer.R;
import com.example.ondc_buyer.model.Product;
import com.example.ondc_buyer.utils.CartManager;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnQuantityChangeListener {
        void onQuantityChanged(Product product, int newQuantity);
    }

    private List<Product> products = new ArrayList<>();
    private final OnQuantityChangeListener listener;

    public ProductAdapter(OnQuantityChangeListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        if (product == null) return;

        holder.textProductName.setText(product.getName() != null ? product.getName() : "Product");

        // Description
        String desc = product.getDescription();
        if (desc != null && !desc.isEmpty()) {
            holder.textProductDescription.setText(desc);
            holder.textProductDescription.setVisibility(View.VISIBLE);
        } else {
            holder.textProductDescription.setVisibility(View.GONE);
        }

        // Price - safe parsing
        try {
            holder.textProductPrice.setText(String.format(Locale.getDefault(), "₹%.2f", product.getPrice()));
        } catch (Exception e) {
            holder.textProductPrice.setText("₹0.00");
        }

        // Product Initial Letter
        String name = product.getName();
        if (name != null && !name.isEmpty()) {
            holder.textProductInitial.setText(String.valueOf(name.charAt(0)).toUpperCase());
        } else {
            holder.textProductInitial.setText("?");
        }

        // Product Image - show initial letter by default, image if URL exists
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            holder.imageProduct.setVisibility(View.VISIBLE);
            holder.textProductInitial.setVisibility(View.GONE);
            try {
                // Use Glide if available
                com.bumptech.glide.Glide.with(holder.itemView.getContext())
                        .load(product.getImageUrl())
                        .centerCrop()
                        .into(holder.imageProduct);
            } catch (Exception e) {
                holder.imageProduct.setVisibility(View.GONE);
                holder.textProductInitial.setVisibility(View.VISIBLE);
            }
        } else {
            holder.imageProduct.setVisibility(View.GONE);
            holder.textProductInitial.setVisibility(View.VISIBLE);
        }

        // Cart quantity
        int quantity = CartManager.getInstance().getQuantity(product);
        if (quantity > 0) {
            holder.btnAdd.setVisibility(View.GONE);
            holder.layoutQuantityControls.setVisibility(View.VISIBLE);
            holder.textQuantity.setText(String.valueOf(quantity));
        } else {
            holder.btnAdd.setVisibility(View.VISIBLE);
            holder.layoutQuantityControls.setVisibility(View.GONE);
        }

        holder.btnAdd.setOnClickListener(v -> {
            if (listener != null) listener.onQuantityChanged(product, 1);
            notifyItemChanged(position);
        });

        holder.btnPlus.setOnClickListener(v -> {
            int qty = CartManager.getInstance().getQuantity(product);
            if (listener != null) listener.onQuantityChanged(product, qty + 1);
            notifyItemChanged(position);
        });

        holder.btnMinus.setOnClickListener(v -> {
            int qty = CartManager.getInstance().getQuantity(product);
            if (listener != null) listener.onQuantityChanged(product, qty - 1);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView textProductInitial, textProductName, textProductDescription, textProductPrice;
        MaterialButton btnAdd;
        LinearLayout layoutQuantityControls;
        ImageButton btnPlus, btnMinus;
        TextView textQuantity;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.image_product);
            textProductInitial = itemView.findViewById(R.id.text_product_initial);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textProductDescription = itemView.findViewById(R.id.text_product_description);
            textProductPrice = itemView.findViewById(R.id.text_product_price);
            btnAdd = itemView.findViewById(R.id.btn_add);
            layoutQuantityControls = itemView.findViewById(R.id.layout_quantity_controls);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            textQuantity = itemView.findViewById(R.id.text_quantity);
        }
    }
}
