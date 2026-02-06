package com.javainstitute.currencymonitor;

import java.util.Map;

public class CurrencyResponse {
    // These variable names must match the JSON keys from the API exactly
    public String result;
    public String base_code;
    public Map<String, Double> conversion_rates;
}