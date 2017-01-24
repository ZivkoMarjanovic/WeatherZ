package com.example.zivko.weather;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;

import db.DataBaseHelper;
import db.MyLocation;

/**
 * Created by Živko on 2016-12-11.
 */

public class AddLocation extends AppCompatActivity {

    DataBaseHelper dataBaseHelper;
    Place newPlace = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_location);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);

        Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newPlace != null ) {
                    double lat = newPlace.getLatLng().latitude;
                    double lon = newPlace.getLatLng().longitude;
                    String name = (String) newPlace.getName();

                    MyLocation newLocation = new MyLocation(name, lat, lon);

                    try {

                        getDatabaseHelper().getMyLocationDao().create(newLocation);

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }

                finish();
            }
        });

        Button back = (Button) findViewById(R.id.back2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
               newPlace = place;
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(AddLocation.this, "Error",Toast.LENGTH_LONG).show();
                 }
        });
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

}
