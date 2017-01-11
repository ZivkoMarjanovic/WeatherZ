package com.example.zivko.weather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.Toast;

import net.Setvice;

import java.util.HashMap;
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

    ListView hourlyList;
    int dt;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hourly_forecast);

        dt = getIntent().getIntExtra("dt", 0);
        id = getIntent().getIntExtra("id", 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setTitle("WeatherZ");
        toolbar.setTitleTextColor(getResources().getColor(R.color.title));
        toolbar.setBackgroundColor(getResources().getColor(R.color.toolbar));
        setSupportActionBar(toolbar);

        Map<String, String> query = new HashMap<String, String>();
        query.put("id",Integer.toString(id));

        query.put("APPID", "6e29ee73099af2921024c44b9814f7bf");

        Call<Forecast> call = Setvice.apiInterface().getForecast(query);
        call.enqueue(new Callback<Forecast>() {
            @Override
            public void onResponse(Call<Forecast> call, Response<Forecast> response) {

                Toast.makeText(getApplicationContext(), Integer.toString(response.code()), LENGTH_LONG).show();


                if (response.code() == 200){
                    final Forecast forecast = response.body();
                    hourlyList = (ListView) findViewById(R.id.hourly);
                    hourlyList.setAdapter(new HourlyAdapter(HourlyForecast.this, forecast, HourlyForecast.this.dt));



                }
            }

            @Override
            public void onFailure(Call<Forecast> call, Throwable t) {
                //u slucaju da je nesto poslo po zlu, ispisemo sta nije u redu tj sta je poruka greske
                Toast.makeText(HourlyForecast.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}
