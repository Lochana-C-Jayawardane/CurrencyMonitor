package com.javainstitute.currencymonitor;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ExchangeRateApi {
    // The {apiKey} and {base} are placeholders replaced at runtime
    @GET("v6/{apiKey}/latest/{base}")
    Call<CurrencyResponse> getRates(
            @Path("apiKey") String apiKey,
            @Path("base") String base
    );
}