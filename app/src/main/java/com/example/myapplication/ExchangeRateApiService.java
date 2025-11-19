package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ExchangeRateApiService {
    // GET /v1/latest?apikey=YOUR_API_KEY&base_currency=USD
    @GET("v1/latest")
    Call<ExchangeRateResponse> getLatestRates(
            @Query("apikey") String apiKey,
            @Query("base_currency") String baseCurrency
    );
}
