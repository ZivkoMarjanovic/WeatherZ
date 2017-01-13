package com.example.zivko.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import net.Setvice;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import base_adapters.HourlyAdapter;
import base_adapters.MyLocationsAdapter;
import db.DataBaseHelper;
import db.MyLocation;
import model_forecast.Forecast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by Živko on 2016-12-11.
 */

public class MyLocations extends AppCompatActivity implements recycle_adapters.MyLocationsAdapter.MyLocationsOnItemClickListener {

    DataBaseHelper dataBaseHelper;
    List<MyLocation> myLocationList;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_locations);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        toolbar.setBackgroundColor(getResources().getColor(R.color.toolbar));
        setSupportActionBar(toolbar);

        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button addLocation = (Button) findViewById(R.id.addlocation);
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyLocations.this, AddLocation.class);
                startActivity(intent);
            }
        });

        /*MyLocationsAdapter locationAdapter = new MyLocationsAdapter(this);
        ListView listView = (ListView) findViewById(R.id.location_lists);
        listView.setAdapter(locationAdapter);*/
        recyclerView = (RecyclerView) findViewById(R.id.location_lists);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new recycle_adapters.MyLocationsAdapter(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshLocationAdapter();
    }

    private void refreshLocationAdapter() {

        RecyclerView recycleView = (RecyclerView) findViewById(R.id.location_lists);

        if (recycleView != null) {
            recycle_adapters.MyLocationsAdapter adapter = (recycle_adapters.MyLocationsAdapter) recycleView.getAdapter();

            if (adapter != null) {
                try {
                    List<MyLocation> list = getDatabaseHelper().getMyLocationDao().queryForAll();
                    adapter.refreshMyLocationsAdapter(list);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public DataBaseHelper getDatabaseHelper() {
        if (dataBaseHelper == null) {
            dataBaseHelper = OpenHelperManager.getHelper(this, DataBaseHelper.class);
        }
        return dataBaseHelper;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (dataBaseHelper != null) {
            OpenHelperManager.releaseHelper();
            dataBaseHelper = null;
        }
    }

    @Override
    public void MyLocationsOnClick(int i, List<MyLocation> myLocationList) {
        if ( i > 0) {
            SharedPreferences st = PreferenceManager.getDefaultSharedPreferences(this);

            String sharedLocation = st.getString("message", getResources().getString(R.string.currentLocation));

            if (sharedLocation.equals(myLocationList.get(i).getName())) {

                SharedPreferences.Editor editor = st.edit();
                editor.putString("message", getResources().getString(R.string.currentLocation));
                editor.commit();
            }
            try {
                getDatabaseHelper().getMyLocationDao().delete(myLocationList.get(i));
                recycle_adapters.MyLocationsAdapter adapter = (recycle_adapters.MyLocationsAdapter) recyclerView.getAdapter();
                refreshLocationAdapter();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
}
