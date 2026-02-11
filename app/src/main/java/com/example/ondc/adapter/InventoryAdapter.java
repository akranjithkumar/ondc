package com.example.ondc.adapter;

import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ondc.R;
import com.example.ondc.databinding.ItemInventoryBinding;
import com.example.ondc.model.InventoryItem;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private List<InventoryItem> items;

    public InventoryAdapter(List<InventoryItem> items) {
        this.items = items;
    }

    public void updateItems(List<InventoryItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemInventoryBinding binding = ItemInventoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new InventoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    class InventoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemInventoryBinding binding;

        InventoryViewHolder(ItemInventoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(InventoryItem item) {
            // Product name
            binding.tvProductName.setText(item.getProductName() != null ? item.getProductName() : "Unknown Product");

            // SKU
            binding.tvSku.setText("SKU: " + (item.getProductSku() != null ? item.getProductSku() : "N/A"));

            // Outlet
            binding.tvOutlet.setText(item.getOutletName() != null ? item.getOutletName() : "");

            // Stock numbers
            int available = item.getAvailableStock() != null ? item.getAvailableStock() : 0;
            int reserved = item.getReservedStock() != null ? item.getReservedStock() : 0;
            int total = item.getTotalStock() != null ? item.getTotalStock() : 0;
            int reorder = item.getReorderLevel() != null ? item.getReorderLevel() : 0;

            binding.tvAvailable.setText(String.valueOf(available));
            binding.tvReserved.setText(String.valueOf(reserved));
            binding.tvReorderLevel.setText(String.valueOf(reorder));
            binding.tvTotalStock.setText("Total Stock: " + total);

            // Progress bar
            int percentage = item.getStockPercentage();
            binding.stockProgress.setProgress(percentage);

            // Color the progress bar based on health
            int healthLevel = item.getStockHealthLevel();
            int progressColor;
            switch (healthLevel) {
                case 0: // critical
                    progressColor = itemView.getContext().getColor(R.color.stock_critical);
                    break;
                case 1: // low
                    progressColor = itemView.getContext().getColor(R.color.stock_low);
                    break;
                default: // healthy
                    progressColor = itemView.getContext().getColor(R.color.stock_healthy);
                    break;
            }
            binding.stockProgress.setProgressTintList(ColorStateList.valueOf(progressColor));

            // Available text color
            binding.tvAvailable.setTextColor(progressColor);

            // Low stock badge
            Boolean isLow = item.getIsLowStock();
            if (isLow != null && isLow) {
                binding.tvLowStockBadge.setVisibility(View.VISIBLE);
                int badgeColor;
                if (healthLevel == 0) {
                    binding.tvLowStockBadge.setText("CRITICAL");
                    badgeColor = itemView.getContext().getColor(R.color.stock_critical);
                } else {
                    binding.tvLowStockBadge.setText("LOW");
                    badgeColor = itemView.getContext().getColor(R.color.stock_low);
                }
                GradientDrawable bg = new GradientDrawable();
                bg.setShape(GradientDrawable.RECTANGLE);
                bg.setCornerRadius(8 * itemView.getContext().getResources().getDisplayMetrics().density);
                bg.setColor(badgeColor);
                binding.tvLowStockBadge.setBackground(bg);
            } else {
                binding.tvLowStockBadge.setVisibility(View.GONE);
            }
        }
    }
}
