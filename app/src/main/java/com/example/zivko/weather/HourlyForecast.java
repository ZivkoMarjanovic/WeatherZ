package com.example.zivko.weather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.Setvice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import base_adapters.HourlyAdapter;
import model_forecast.Forecast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by Živko on 2016-12-11.
 */

public class HourlyForecast extends AppCompatActivity {

    RecyclerView hourlyList;
    int dt;
    double lat;
    double lon;
    Call<Forecast> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hourly_forecast);


        dt = getIntent().getIntExtra("dt", 0);
        lat = getIntent().getDoubleExtra("lat", 0.0);
        lon = getIntent().getDoubleExtra("lon", 0.0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setBackgroundColor(getResources().getColor(R.color.toolbar));
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);


        TextView dayInWeek = (TextView) findViewById(R.id.lon);
        long newDT = (long) dt;
        Date p = new Date(newDT * 1000);
        dayInWeek.setText(new SimpleDateFormat("EEEE", Locale.getDefault()).format(p));

        Map<String, Double> query2 = new HashMap<String, Double>();
        query2.put("lat", lat);
        query2.put("lon", lon);

        Map<String, String> query = new HashMap<String, String>();
        query.put("APPID", "6e29ee73099af2921024c44b9814f7bf");

        call = Setvice.apiInterface().getForecast(query2 ,query);
        call.enqueue(new Callback<Forecast>() {
            @Override
            public void onResponse(Call<Forecast> call, Response<Forecast> response) {

                //Toast.makeText(getApplicationContext(), Integer.toString(response.code()), LENGTH_LONG).show();


                if (response.code() == 200) {
                    final Forecast forecast = response.body();

                    hourlyList = (RecyclerView) findViewById(R.id.hourly);
                    hourlyList.setLayoutManager(new LinearLayoutManager(HourlyForecast.this));
                    hourlyList.setAdapter(new recycle_adapters.HourlyAdapter(forecast, dt));
                }
            }

            @Override
            public void onFailure(Call<Forecast> call, Throwable t) {

                Toast.makeText(HourlyForecast.this, getResources().getString(R.string.noInternet), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        call.cancel();
        super.onDestroy();
    }
}
