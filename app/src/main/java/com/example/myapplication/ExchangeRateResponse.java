package com.example.myapplication;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

// Updated to match freecurrencyapi.com response structure: {"data": {"USD": 1.0, ...}}
public class ExchangeRateResponse {
    @SerializedName("data")
    private Map<String, Double> data;

    public Map<String, Double> getData() {
        return data;
    }
}
