package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.List;

public class CurrencyAdapter extends ArrayAdapter<String> {

    private final List<String> currencies;

    public CurrencyAdapter(@NonNull Context context, List<String> currencies) {
        super(context, 0, currencies);
        this.currencies = currencies;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item_with_flag, parent, false);
        }

        ImageView flagImageView = convertView.findViewById(R.id.image_view_flag);
        TextView currencyCodeTextView = convertView.findViewById(R.id.text_view_currency_code);

        String currencyCode = currencies.get(position);

        if (currencyCode != null) {
            currencyCodeTextView.setText(currencyCode);

            // Build the URL for the flag image
            // Example: USD -> us.png, BRL -> br.png
            String countryCode = currencyCode.substring(0, 2).toLowerCase();
            String flagUrl = "https://flagcdn.com/w40/" + countryCode + ".png";

            Glide.with(getContext())
                    .load(flagUrl)
                    .error(android.R.drawable.ic_menu_mapmode) // Fallback icon
                    .into(flagImageView);
        }

        return convertView;
    }
}
