package com.example.ondc_buyer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ondc_buyer.R;
import com.example.ondc_buyer.model.Vendor;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class VendorAdapter extends RecyclerView.Adapter<VendorAdapter.VendorViewHolder> {

    public interface OnVendorClickListener {
        void onVendorClick(Vendor vendor);
    }

    private List<Vendor> vendors = new ArrayList<>();
    private final OnVendorClickListener listener;

    public VendorAdapter(OnVendorClickListener listener) {
        this.listener = listener;
    }

    public void setVendors(List<Vendor> vendors) {
        this.vendors = vendors != null ? vendors : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VendorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vendor, parent, false);
        return new VendorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorViewHolder holder, int position) {
        Vendor vendor = vendors.get(position);
        if (vendor == null) return;

        holder.textVendorName.setText(vendor.getName() != null ? vendor.getName() : "Unnamed Store");
        holder.textVendorAddress.setText(vendor.getAddress() != null ? vendor.getAddress() : "—");

        // Phone
        if (vendor.getContactPhone() != null && !vendor.getContactPhone().isEmpty()) {
            holder.textVendorPhone.setText(vendor.getContactPhone());
            holder.layoutContact.setVisibility(View.VISIBLE);
        } else {
            holder.layoutContact.setVisibility(View.GONE);
        }

        // Rating
        Double rating = vendor.getRating();
        if (rating != null) {
            holder.chipRating.setText(String.format("%.1f ★", rating));
            holder.chipRating.setVisibility(View.VISIBLE);
        } else {
            holder.chipRating.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onVendorClick(vendor);
        });
    }

    @Override
    public int getItemCount() {
        return vendors != null ? vendors.size() : 0;
    }

    static class VendorViewHolder extends RecyclerView.ViewHolder {
        TextView textVendorName, textVendorAddress, textVendorPhone;
        Chip chipRating;
        LinearLayout layoutContact;

        VendorViewHolder(@NonNull View itemView) {
            super(itemView);
            textVendorName = itemView.findViewById(R.id.text_vendor_name);
            textVendorAddress = itemView.findViewById(R.id.text_vendor_address);
            textVendorPhone = itemView.findViewById(R.id.text_vendor_phone);
            chipRating = itemView.findViewById(R.id.chip_rating);
            layoutContact = itemView.findViewById(R.id.layout_contact);
        }
    }
}
