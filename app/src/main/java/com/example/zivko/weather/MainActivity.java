package com.example.zivko.weather;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.Setvice;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import base_adapters.DailyAdapter;
import model_daily.Daily;

import model_weather.ExampleNew;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity {
ListView forecastList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        getCurrentWeather();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        toolbar.setTitle("WeatherZ");
        toolbar.setTitleTextColor(getResources().getColor(R.color.title));
        toolbar.setBackgroundColor(getResources().getColor(R.color.toolbar));
        setSupportActionBar(toolbar);




        Map<String, String> query = new HashMap<String, String>();
        query.put("id", "792680");
        query.put("cnt", "5");

        query.put("APPID", "6e29ee73099af2921024c44b9814f7bf");

        Call<Daily> call = Setvice.apiInterface().getDailyForecast(query);
        call.enqueue(new Callback<Daily>() {
            @Override
            public void onResponse(Call<Daily> call, Response<Daily> response) {

               Toast.makeText(MainActivity.this, Integer.toString(response.code()), LENGTH_LONG).show();


                if (response.code() == 200) {
                    final Daily daily = response.body();
                    forecastList = (ListView) findViewById(R.id.forecast);
                    forecastList.setAdapter(new DailyAdapter(MainActivity.this, daily));
                    forecastList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            int dt = daily.getList().get(i).getDt();
                            Intent intent = new Intent(MainActivity.this, HourlyForecast.class);
                            intent.putExtra("dt", dt);
                            intent.putExtra("id", daily.getCity().getId());
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Daily> call, Throwable t) {
                //u slucaju da je nesto poslo po zlu, ispisemo sta nije u redu tj sta je poruka greske
                //Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:

                break;

            case R.id.About:

                break;
            case R.id.refresh:
                getCurrentWeather();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();



    }

    private void getCurrentWeather(){
        Map<String, String> query = new HashMap<String, String>();
        query.put("id","792680");

        query.put("APPID", "6e29ee73099af2921024c44b9814f7bf");

        Call<ExampleNew> call = Setvice.apiInterface().getExample(query);
        call.enqueue(new Callback<ExampleNew>() {
            @Override
            public void onResponse(Call<ExampleNew> call, Response<ExampleNew> response) {

                Toast.makeText(getApplicationContext(), Integer.toString(response.code()), LENGTH_LONG).show();


                if (response.code() == 200){
                    final ExampleNew example = response.body();

                    final TextView temperature = (TextView) findViewById(R.id.temperature);
                    temperature.setText(String.format(Locale.CANADA,"%.2f",example.getMain().getTemp()-273.15));

                    final TextView pressure = (TextView) findViewById(R.id.pressure);
                    pressure.setText(Integer.toString(example.getMain().getPressure()));

                    final TextView humididty = (TextView) findViewById(R.id.humididty);
                    humididty.setText(Integer.toString(example.getMain().getHumidity()));

                    final TextView temp_max = (TextView) findViewById(R.id.temp_max);
                    temp_max.setText(String.format(Locale.CANADA,"%.2f",example.getMain().getTempMax()-273.15));

                    final TextView wind_speed = (TextView) findViewById(R.id.wind_speed);
                    wind_speed.setText(String.format(Locale.CANADA,"%.2f",example.getWind().getSpeed()));

                    //final TextView wind_direction = (TextView) dialog.findViewById(R.id.wind_direction);
                   // wind_direction.setText(Integer.toString(example.getWind().getDeg()));

                    final TextView cloudiness = (TextView) findViewById(R.id.cloudiness);
                    cloudiness.setText(Integer.toString(example.getClouds().getAll()));

                }
            }

            @Override
            public void onFailure(Call<ExampleNew> call, Throwable t) {

                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}
