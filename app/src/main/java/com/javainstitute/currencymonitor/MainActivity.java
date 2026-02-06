package com.javainstitute.currencymonitor;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.javainstitute.currencymonitor.databinding.ActivityMainBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private CurrencyAdapter adapter;

    // FIX: Removed the line "private MainActivity BuildConfig;"
    // This allows the app to use the REAL generated BuildConfig
    private final String API_KEY = BuildConfig.API_KEY;

    private final Handler autoRefreshHandler = new Handler();
    private Runnable refreshRunnable;
    private final int REFRESH_INTERVAL = 60000; // 1 minute

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. Setup Dropdown
        String[] currencies = {"USD", "LKR", "EUR", "GBP", "JPY", "AUD", "INR"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, currencies);
        binding.baseCurrencySelector.setAdapter(arrayAdapter);

        // 2. Setup RecyclerView
        adapter = new CurrencyAdapter();
        binding.currenciesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.currenciesRecyclerView.setAdapter(adapter);

        // 3. Selection Listener
        binding.baseCurrencySelector.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            fetchRates(selected);
        });

        // 4. Search View Implementation
        binding.searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });

        // 5. Auto-Refresh Logic
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                String currentBase = binding.baseCurrencySelector.getText().toString();
                if (currentBase.isEmpty()) currentBase = "USD";
                fetchRates(currentBase);
                autoRefreshHandler.postDelayed(this, REFRESH_INTERVAL);
            }
        };

        // Initial fetch
        fetchRates("USD");
    }

    private void fetchRates(String base) {
        // Validation to prevent crash if API key is missing
        if (API_KEY == null || API_KEY.isEmpty() || API_KEY.equals("YOUR_API_KEY")) {
            Toast.makeText(this, "Error: Valid API Key Required", Toast.LENGTH_LONG).show();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://v6.exchangerate-api.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ExchangeRateApi api = retrofit.create(ExchangeRateApi.class);

        api.getRates(API_KEY, base).enqueue(new Callback<CurrencyResponse>() {
            @Override
            public void onResponse(Call<CurrencyResponse> call, Response<CurrencyResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setRates(response.body().conversion_rates);

                    // Add Time Stamp
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault());
                    String currentTime = sdf.format(new java.util.Date());
                    binding.lastUpdatedText.setText("Last Sync: " + currentTime + " (" + base + ")");
                }
            }

            @Override
            public void onFailure(Call<CurrencyResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Update Failed: Check Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        autoRefreshHandler.post(refreshRunnable); // Start auto-refresh
    }

    @Override
    protected void onPause() {
        super.onPause();
        autoRefreshHandler.removeCallbacks(refreshRunnable); // Stop auto-refresh
    }
}