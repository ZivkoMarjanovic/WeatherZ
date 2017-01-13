package com.example.zivko.weather;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.squareup.picasso.Picasso;

import net.Setvice;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import base_adapters.DailyAdapter;
import db.DataBaseHelper;
import db.MyLocation;
import intent_service.FetchAddressIntentService;
import intent_service.MyResultReceiver;
import model_daily.Daily;

import model_weather.ExampleNew;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity implements
        LocationListener, MyResultReceiver.Receiver, recycle_adapters.DailyAdapter.DailyOnItemClickListener {//, SharedPreferences.OnSharedPreferenceChangeListener {

    private LocationManager locationManager;
    private String provider;
    ListView forecastList;
    double lat;
    double lon;
    String name;
    Location myLocation;
    DataBaseHelper dataBaseHelper;
    SharedPreferences prefs;
    TextView city;
    ImageView icon;
    TextView temperature;
    TextView pressure;
    TextView humididty;
    TextView temp_max;
    TextView wind_speed;
    TextView cloudiness;
    Call<Daily> call1;
    Call<ExampleNew> call2;
    MyResultReceiver mReceiver;
    RecyclerView dailyRecycleView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean net = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!net || !gps) {
            Toast.makeText(this, String.valueOf(R.string.noInternet), LENGTH_LONG).show();
            final Dialog turn_on = new Dialog(this);
            turn_on.setContentView(R.layout.turn_on_gps);
            turn_on.setCanceledOnTouchOutside(false);

            Button no = (Button) turn_on.findViewById(R.id.no);
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    turn_on.dismiss();
                }
            });

            Button ok = (Button) turn_on.findViewById(R.id.ok);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    turn_on.dismiss();
                }
            });
            turn_on.show();

            Window window = turn_on.getWindow();
            if (turn_on.isShowing()) {
                window.setBackgroundDrawableResource(R.color.title1);
            }
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(window.getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(lp);

        }
        setContentView(R.layout.main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        toolbar.setTitle(R.string.app_name);
        toolbar.setBackgroundColor(getResources().getColor(R.color.toolbar));
        setSupportActionBar(toolbar);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        city = (TextView) findViewById(R.id.address);
        icon = (ImageView) findViewById(R.id.mainIcon);
        temperature = (TextView) findViewById(R.id.temperature);
        pressure = (TextView) findViewById(R.id.pressure);
        humididty = (TextView) findViewById(R.id.humididty);
        temp_max = (TextView) findViewById(R.id.temp_max);
        wind_speed = (TextView) findViewById(R.id.wind_speed);
        cloudiness = (TextView) findViewById(R.id.cloudiness);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.mainRow);
        linearLayout.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            @Override
            public void onSwipeLeft() {
                try {
                    List<MyLocation> swipeLeft = getDatabaseHelper().getMyLocationDao().queryForAll();
                    List<String> leftLocations = new ArrayList<>();

                    for (MyLocation m : swipeLeft) {
                        leftLocations.add(m.getName());
                    }
                    int indexLeft;
                    if (leftLocations.contains(name)) {
                        indexLeft = leftLocations.indexOf(name);
                    } else {
                        indexLeft = 0;
                    }


                    if (indexLeft < swipeLeft.size()) {

                        indexLeft++;

                        MyLocation nextLeft = swipeLeft.get(indexLeft);
                        MainActivity.this.lat = nextLeft.getLat();
                        MainActivity.this.lon = nextLeft.getLon();
                        setName(nextLeft.getName());

                        getCurrentWeather();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onSwipeRight() {
                try {
                    List<MyLocation> swipeRight = getDatabaseHelper().getMyLocationDao().queryForAll();
                    List<String> rightLocations = new ArrayList<>();

                    for (MyLocation m : swipeRight) {
                        rightLocations.add(m.getName());
                    }

                    int indexRight;
                    if (rightLocations.contains(name)) {
                        indexRight = rightLocations.indexOf(name);
                    } else {
                        indexRight = 0;
                    }

                    if (indexRight >= 0) {

                        indexRight--;

                        MyLocation nextLeft = swipeRight.get(indexRight);
                        MainActivity.this.lat = nextLeft.getLat();
                        MainActivity.this.lon = nextLeft.getLon();
                        setName(nextLeft.getName());

                        getCurrentWeather();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        mReceiver = new MyResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        getLocationFromSharedPreferences();
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setMyLocation() {
        myLocation = getLocationFromManger();
        if (myLocation != null) {
            getNameFromAddress(myLocation.getLatitude(), myLocation.getLongitude());
        } else {
            firstInput();
        }
    }

    private void firstInput() {
        String address = prefs.getString("address", null);

        if (address == null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("address", getResources().getString(R.string.noLocation));
            String sh = prefs.getString("message", null);
            if (sh == null) {
                editor.putString("message", getResources().getString(R.string.currentLocation));
            }
            editor.commit();

            MyLocation myLocationn = new MyLocation();
            myLocationn.setName(getResources().getString(R.string.currentLocation));
            myLocationn.setLon(500);
            myLocationn.setLat(500);
            myLocationn.setId(1);

            MyLocation sanFrancisco = new MyLocation(getResources().getString(R.string.sanFrancisco), 37.773972, -122.431297);
            MyLocation barcelona = new MyLocation(getResources().getString(R.string.barcelona), 41.390205, 2.154007);

            List<MyLocation> checkLocation = new ArrayList<>();
            try {
                checkLocation = getDatabaseHelper().getMyLocationDao().queryBuilder().where().eq(MyLocation.ID_LOCATION, 1).query();
                if (checkLocation.isEmpty()) {
                    getDatabaseHelper().getMyLocationDao().create(myLocationn);
                    getDatabaseHelper().getMyLocationDao().create(sanFrancisco);
                    getDatabaseHelper().getMyLocationDao().create(barcelona);
                } else {
                    getDatabaseHelper().getMyLocationDao().update(myLocationn);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            name = getResources().getString(R.string.noLocation);
            lat = 500;
            lon = 500;
            city.setText(name);
        }
    }


    private Location getLocationFromManger() {
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        locationManager.requestLocationUpdates(provider, 0, 0, this);

        Location fromManger = locationManager.getLastKnownLocation(provider);
        return fromManger;
    }

    private void getLocationFromSharedPreferences() {
       String sharedLocation = prefs.getString("message", null);
        if (sharedLocation != null) {
            try {
                MyLocation mySharedLocation = getDatabaseHelper().getMyLocationDao().queryBuilder().where().eq(MyLocation.NAME, sharedLocation).query().get(0);
                this.lat = mySharedLocation.getLat();
                this.lon = mySharedLocation.getLon();
                setName(mySharedLocation.getName());
                getCurrentWeather();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            city.setText(getResources().getString(R.string.looking));
            setMyLocation();
        }
    }


    private void getNameFromAddress(double myLat, double myLon) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra("lat", myLat);
        intent.putExtra("lon", myLon);
        intent.putExtra("receiverTag", mReceiver);
        startService(intent);
    }


    private void setName(String newName) {
        if (newName.equals(getResources().getString(R.string.currentLocation))|| newName.equals(getResources().getString(R.string.noLocation))) {
            String address = prefs.getString("address", null);

            if (address != null) {
                name = address;
            } else {name = newName;}
            setMyLocation();
        }else {
            name = newName;
        }
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
            case R.id.addlocation:
                Intent myLocationIntent = new Intent(MainActivity.this, MyLocations.class);
                startActivity(myLocationIntent);
                break;

            case R.id.settings:
                Intent intent = new Intent(MainActivity.this, com.example.zivko.weather.Settings.class);
                startActivity(intent);
                break;

            case R.id.About:
                About about = new About();
                about.show(getSupportFragmentManager(), getResources().getString(R.string.about));

                break;

            case R.id.refresh:
                String address = prefs.getString("address", null);

                if (name.equals(getResources().getString(R.string.noLocation))) {
                    setMyLocation();
                } else {
                    if (name.equals(address)) {
                        getCurrentWeather();
                        setMyLocation();
                    } else {
                        getCurrentWeather();
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getDailyForecast() {
        if (isNetworkAvailable() && lat < 400 && lon < 400) {
            Map<String, Double> query2 = new HashMap<String, Double>();
            query2.put("lat", lat);
            query2.put("lon", lon);

            Map<String, String> query = new HashMap<String, String>();
            /*query.put("lat", String.format(Locale.ENGLISH, "%.6f", this.lat));
            query.put("lon", String.format(Locale.ENGLISH, "%.6f", this.lon));*/
            query.put("cnt", Integer.toString(6));
            query.put("APPID", "6e29ee73099af2921024c44b9814f7bf");

            call1 = Setvice.apiInterface().getDailyForecast(query2, query);
            call1.enqueue(new Callback<Daily>() {
                @Override
                public void onResponse(Call<Daily> call, Response<Daily> response) {

                    if (response.code() == 200) {
                        final Daily daily = response.body();

                        dailyRecycleView = (RecyclerView) findViewById(R.id.forecast);
                        if (dailyRecycleView != null) {
                            recycle_adapters.DailyAdapter adapter = (recycle_adapters.DailyAdapter) dailyRecycleView.getAdapter();
                            if (adapter != null) {
                                adapter.updateDaily(daily);
                            } else {
                                dailyRecycleView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                                recycle_adapters.DailyAdapter newAdapter = new recycle_adapters.DailyAdapter(daily, MainActivity.this);
                                dailyRecycleView.setAdapter(newAdapter);
                            }
                        }
                        /*forecastList = (ListView) findViewById(R.id.forecast);
                        if (forecastList != null) {
                            DailyAdapter adapter = (DailyAdapter) forecastList.getAdapter();

                            if (adapter != null) {
                                adapter.refreshDailyAdapter(daily);
                            } else {
                                forecastList.setAdapter(new DailyAdapter(MainActivity.this, daily));
                            }
                            forecastList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    int dt = daily.getList().get(i).getDt();
                                    Intent intent = new Intent(MainActivity.this, HourlyForecast.class);
                                    intent.putExtra("dt", dt);
                                    intent.putExtra("lat", daily.getCity().getCoord().getLat());
                                    intent.putExtra("lon", daily.getCity().getCoord().getLon());
                                    startActivity(intent);
                                }
                            });
                        }*/
                    } else {
                       noDailyInfo();
                    }

                }

                @Override
                public void onFailure(Call<Daily> call, Throwable t) {
                    noDailyInfo();
                   // Toast.makeText(MainActivity.this, t.getStackTrace()[0].toString(), Toast.LENGTH_SHORT).show();
                    //org.gradle.jvmargs=-Xmx1536m
                }
            });
        } else {
            noDailyInfo();
        }
    }

    private void noDailyInfo() {

        dailyRecycleView = (RecyclerView) findViewById(R.id.forecast);
        if(dailyRecycleView != null) {
            dailyRecycleView.setAdapter(null);
        }
        /*forecastList = (ListView) findViewById(R.id.forecast);
        if (forecastList != null) {
            forecastList.setAdapter(null);
        }*/

    }

    private void getCurrentWeather() {
        city.setText(name);

        if (isNetworkAvailable() && lat < 400 && lon < 400) {
            Map<String, Double> query2 = new HashMap<String, Double>();
            query2.put("lat", lat);
            query2.put("lon", lon);

            Map<String, String> query = new HashMap<String, String>();
            query.put("APPID", "6e29ee73099af2921024c44b9814f7bf");

            call2 = Setvice.apiInterface().getExample(query2, query);
            call2.enqueue(new Callback<ExampleNew>() {
                @Override
                public void onResponse(Call<ExampleNew> call, Response<ExampleNew> response) {
                    if (response.code() == 200) {
                        getDailyForecast();
                        final ExampleNew example = response.body();

                        Picasso.with(MainActivity.this)
                                .load("http://openweathermap.org/img/w/" + example.getWeather().get(0).getIcon() + ".png")
                                .into(icon);

                        temperature.setText(String.format(Locale.getDefault(), "%.2f", example.getMain().getTemp() - 273.15));

                        pressure.setText(Integer.toString(example.getMain().getPressure()));

                        humididty.setText(Integer.toString(example.getMain().getHumidity()));

                        temp_max.setText(String.format(Locale.getDefault(), "%.2f", example.getMain().getTempMax() - 273.15));

                        wind_speed.setText(String.format(Locale.getDefault(), "%.2f", example.getWind().getSpeed()));

                        cloudiness.setText(Integer.toString(example.getClouds().getAll()));

                    } else {
                        noInternetCurrent();
                    }
                }

                @Override
                public void onFailure(Call<ExampleNew> call, Throwable t) {
                    noInternetCurrent();
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.noInternet), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            noInternetCurrent();
           }

    }

    private void noInternetCurrent() {
        if (lat < 400 && lon < 400) {
            icon.setImageDrawable(getResources().getDrawable(R.drawable.no_internet2));
        }

        temperature.setText(getResources().getString(R.string.empty));

        pressure.setText(getResources().getString(R.string.empty));

        humididty.setText(getResources().getString(R.string.empty));

        temp_max.setText(getResources().getString(R.string.empty));

        wind_speed.setText(getResources().getString(R.string.empty));

        cloudiness.setText(getResources().getString(R.string.empty));

        noDailyInfo();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver.setReceiver(this);
        List<MyLocation> change = new ArrayList<>();
       String address = prefs.getString("address", null);
        try {
            change = getDatabaseHelper().getMyLocationDao().queryBuilder().where().eq(MyLocation.NAME, name).query();
            if (change.isEmpty()&& (!name.equals( address))) {
                getLocationFromSharedPreferences();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        //  prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mReceiver.setReceiver(null);
        // prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            MyLocation myLocationn = new MyLocation();
            myLocationn.setName(getResources().getString(R.string.currentLocation));
            myLocationn.setLat(location.getLatitude());
            myLocationn.setLon(location.getLongitude());
            myLocationn.setId(1);

            List<MyLocation> checkLocation = new ArrayList<>();
            checkLocation = getDatabaseHelper().getMyLocationDao().queryBuilder().where().eq(MyLocation.ID_LOCATION, 1).query();

            if (checkLocation.isEmpty()) {
                getDatabaseHelper().getMyLocationDao().create(myLocationn);
            } else {
                getDatabaseHelper().getMyLocationDao().update(myLocationn);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public DataBaseHelper getDatabaseHelper() {
        if (dataBaseHelper == null) {
            dataBaseHelper = OpenHelperManager.getHelper(this, DataBaseHelper.class);
        }
        return dataBaseHelper;
    }

    @Override
    public void onDestroy() {
        if (call1 != null) {
            call1.cancel();
        }
        if (call2 != null) {
            call2.cancel();
        }
        super.onDestroy();
        if (dataBaseHelper != null) {
            OpenHelperManager.releaseHelper();
            dataBaseHelper = null;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultCode == 1) {
            String address = resultData.getString("result");
            double myLat = resultData.getDouble("lat");
            double myLon = resultData.getDouble("lon");

            MyLocation myLocationn = new MyLocation();
            myLocationn.setName(getResources().getString(R.string.currentLocation));
            myLocationn.setLat(myLat);
            myLocationn.setLon(myLon);
            myLocationn.setId(1);

            List<MyLocation> checkLocation = new ArrayList<>();
            try {
                checkLocation = getDatabaseHelper().getMyLocationDao().queryBuilder().where().eq(MyLocation.ID_LOCATION, 1).query();

                if (checkLocation.isEmpty()) {
                    getDatabaseHelper().getMyLocationDao().create(myLocationn);
                } else {
                    getDatabaseHelper().getMyLocationDao().update(myLocationn);
                }

               String oldAddress = prefs.getString("address", null);
                String sh = prefs.getString("message", null);

                SharedPreferences.Editor editor = prefs.edit();
                if (sh == null) {
                    editor.putString("message", getResources().getString(R.string.currentLocation));
                }
                editor.putString("address", address);
                editor.commit();

                if (oldAddress == null || name.equals(oldAddress)) {
                    name = address;
                    lat = myLat;
                    lon = myLon;
                    getCurrentWeather();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            firstInput();
        }
    }

    @Override
    public void DailyOnClick(int position, Daily mydaily) {
        int dt = mydaily.getList().get(position).getDt();
        Intent intent = new Intent(MainActivity.this, HourlyForecast.class);
        intent.putExtra("dt", dt);
        intent.putExtra("lat", mydaily.getCity().getCoord().getLat());
        intent.putExtra("lon", mydaily.getCity().getCoord().getLon());
        startActivity(intent);
    }

    /*@Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        setMyLocation();
    }*/
}
