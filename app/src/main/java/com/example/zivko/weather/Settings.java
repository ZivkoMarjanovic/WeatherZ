package com.example.zivko.weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DataBaseHelper;
import db.MyLocation;

/**
 * Created by Živko on 2016-12-14.
 */

public class Settings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{


    List<MyLocation> locations;
    DataBaseHelper dataBaseHelper;
    private AppCompatDelegate mDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        toolbar.setTitle(getResources().getString(R.string.weather_settings));
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar3));

        addPreferencesFromResource(R.xml.settings);

        try {

            locations = getDatabaseHelper().getMyLocationDao().queryForAll();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<String> listLocations = new ArrayList<>();
        for (MyLocation m: locations) {
            listLocations.add(m.getName());
        }
        String [] myLocations = new String [locations.size()];

        listLocations.toArray(myLocations);
        ListPreference lp = (ListPreference)findPreference("message");
        lp.setEntries(myLocations);
        lp.setEntryValues(myLocations);
        lp.setDefaultValue(getResources().getString(R.string.currentLocation));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sh = sharedPreferences.getString("message", null);
        lp.setSummary(sh);


    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        updatePrefSummary(findPreference(s));
    }
    private void updatePrefSummary(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            p.setSummary(listPref.getEntry());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
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
        getDelegate().onDestroy();

        if (dataBaseHelper != null) {
            OpenHelperManager.releaseHelper();
            dataBaseHelper = null;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    private void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }



}
