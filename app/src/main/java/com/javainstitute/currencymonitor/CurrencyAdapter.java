package com.javainstitute.currencymonitor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder> {
    private List<Map.Entry<String, Double>> ratesList = new ArrayList<>();
//    private List<Map.Entry<String, Double>> ratesListFull = new ArrayList<>(); // Full copy

//    public void setRates(Map<String, Double> rates) {
//        this.ratesList = new ArrayList<>(rates.entrySet());
//        notifyDataSetChanged(); // Refresh the list
//    }
// Inside CurrencyAdapter.java
private List<Map.Entry<String, Double>> ratesListFull = new ArrayList<>(); // Full copy

    public void setRates(Map<String, Double> rates) {
        this.ratesList = new ArrayList<>(rates.entrySet());
        this.ratesListFull = new ArrayList<>(this.ratesList); // Store the original
        notifyDataSetChanged();
    }

    public void filter(String text) {
        List<Map.Entry<String, Double>> filteredList = new ArrayList<>();
        for (Map.Entry<String, Double> item : ratesListFull) {
            // Check if currency code matches search text
            if (item.getKey().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        this.ratesList = filteredList;
        notifyDataSetChanged(); // Update the UI
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_currency, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map.Entry<String, Double> entry = ratesList.get(position);
        String currencyCode = entry.getKey();

        holder.codeText.setText(currencyCode);
        holder.valueText.setText(String.format("%.2f", entry.getValue()));

        // Improved Flag Mapping
        String countryCode = getCountryCodeFromCurrency(currencyCode);
        String flagUrl = "https://flagcdn.com/w160/" + countryCode + ".png";

        // Modern Image Loading with Crossfade
        Glide.with(holder.itemView.getContext())
                .load(flagUrl)
                .transition(DrawableTransitionOptions.withCrossFade()) // Smooth fade-in
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .centerCrop()
                .into(holder.flagImage);

        // Add a slide-in animation for the rows
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.slide_in_left));
    }

    // Helper method to fix the "substring" issue for common global currencies
    private String getCountryCodeFromCurrency(String currencyCode) {
        switch (currencyCode) {
            case "USD": return "us";
            case "EUR": return "eu";
            case "JPY": return "jp";
            case "GBP": return "gb";
            case "AUD": return "au";
            case "CAD": return "ca";
            case "CHF": return "ch";
            case "CNY": return "cn";
            case "LKR": return "lk";
            case "INR": return "in";
            case "AED": return "ae";
            default:
                // Fallback: Use first two letters for others
                return currencyCode.substring(0, 2).toLowerCase();
        }
    }

    @Override
    public int getItemCount() {
        return ratesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView codeText, valueText;
        ImageView flagImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            codeText = itemView.findViewById(R.id.currencyCode);
            valueText = itemView.findViewById(R.id.currencyValue);
            flagImage = itemView.findViewById(R.id.currencyFlag);
        }
    }
}