package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CurrencyConverterActivity extends BaseActivity {

    private EditText editTextAmount;
    private Spinner spinnerFromCurrency, spinnerToCurrency;
    private Button buttonConvert;
    private TextView textViewResult;

    private ExchangeRateApiService apiService;
    private Map<String, Double> conversionRates;

    private final String API_KEY = "fca_live_jDc4X2qEBLALEYpBuCMXTAlYHjIqhIQbvYaaeLrx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_converter);

        editTextAmount = findViewById(R.id.edit_text_amount);
        spinnerFromCurrency = findViewById(R.id.spinner_from_currency);
        spinnerToCurrency = findViewById(R.id.spinner_to_currency);
        buttonConvert = findViewById(R.id.button_convert_currency);
        textViewResult = findViewById(R.id.text_view_currency_result);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.freecurrencyapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ExchangeRateApiService.class);

        buttonConvert.setOnClickListener(v -> convertCurrency());

        fetchConversionRates();
    }

    private void fetchConversionRates() {
        apiService.getLatestRates(API_KEY, "USD").enqueue(new Callback<ExchangeRateResponse>() {
            @Override
            public void onResponse(@NonNull Call<ExchangeRateResponse> call, @NonNull Response<ExchangeRateResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    conversionRates = response.body().getData();
                    setupSpinners();
                } else {
                    Toast.makeText(CurrencyConverterActivity.this, "Failed to fetch rates. Check API key.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ExchangeRateResponse> call, @NonNull Throwable t) {
                Toast.makeText(CurrencyConverterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpinners() {
        if (conversionRates == null) return;

        ArrayList<String> currencyList = new ArrayList<>(conversionRates.keySet());
        Collections.sort(currencyList);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencyList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFromCurrency.setAdapter(adapter);
        spinnerToCurrency.setAdapter(adapter);

        // Set default selections
        spinnerFromCurrency.setSelection(adapter.getPosition("USD"));
        spinnerToCurrency.setSelection(adapter.getPosition("BRL"));
    }

    private void convertCurrency() {
        String amountStr = editTextAmount.getText().toString();
        if (TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        if (conversionRates == null || conversionRates.isEmpty()) {
            Toast.makeText(this, "Exchange rates not loaded yet", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            String fromCurrency = spinnerFromCurrency.getSelectedItem().toString();
            String toCurrency = spinnerToCurrency.getSelectedItem().toString();

            Double fromRate = conversionRates.get(fromCurrency);
            Double toRate = conversionRates.get(toCurrency);
            
            if (fromRate == null || toRate == null) {
                 Toast.makeText(this, "Could not find rates for selected currencies", Toast.LENGTH_SHORT).show();
                 return;
            }

            // Convert amount to USD first, then to the target currency. The base is already USD.
            double convertedAmount = (amount / fromRate) * toRate;

            DecimalFormat df = new DecimalFormat("#.##");
            textViewResult.setText(String.format("%s %s", df.format(convertedAmount), toCurrency));

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
        }
    }
}
