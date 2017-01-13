package base_adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.zivko.weather.MainActivity;
import com.example.zivko.weather.R;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DataBaseHelper;
import db.MyLocation;
import model_daily.Daily;

/**
 * Created by Živko on 2016-11-28.
 */

public class MyLocationsAdapter extends BaseAdapter {

    Context context;
    DataBaseHelper dataBaseHelper;
    List<MyLocation> myLocationList;

    public MyLocationsAdapter(Context context) {
        this.context = context;
        try {

            myLocationList = getDatabaseHelper().getMyLocationDao().queryForAll();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (dataBaseHelper != null) {
            OpenHelperManager.releaseHelper();
            dataBaseHelper = null;
        }
    }

    @Override
    public int getCount() {
        return myLocationList.size();
    }

    @Override
    public Object getItem(int i) {
        return myLocationList.get(i).getName();
    }

    @Override
    public long getItemId(int i) {
        return myLocationList.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.location_list, viewGroup, false);
        setMyLocations(row, i);
        return row;
    }

    private void setMyLocations(final View row, int i) {

        TextView days = (TextView) row.findViewById(R.id.textList);
        days.setText(myLocationList.get(i).getName());

        CheckBox checkBox = (CheckBox) row.findViewById(R.id.checkbox);
        checkBox.setChecked(true);
        checkBox.setTag(i);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox ch = (CheckBox) view;
                int i = (int) ch.getTag();
                if (!ch.isChecked() && i > 0) {
                    SharedPreferences st = PreferenceManager.getDefaultSharedPreferences(context);

                    String sharedLocation = st.getString("message", context.getResources().getString(R.string.currentLocation));

                    if (sharedLocation.equals(myLocationList.get(i).getName())) {

                        SharedPreferences.Editor editor = st.edit();
                        editor.putString("message", context.getResources().getString(R.string.currentLocation));
                        editor.commit();
                    }
                    try {
                        getDatabaseHelper().getMyLocationDao().delete(myLocationList.get(i));
                        myLocationList = getDatabaseHelper().getMyLocationDao().queryForAll();
                        notifyDataSetChanged();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    if (dataBaseHelper != null) {
                        OpenHelperManager.releaseHelper();
                        dataBaseHelper = null;
                    }
                }
            }
        });
    }

    public void refreshAdapter(List<MyLocation> myLocations1) {
        myLocationList = myLocations1;
        notifyDataSetChanged();
    }

    public DataBaseHelper getDatabaseHelper() {
        if (dataBaseHelper == null) {
            dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
        }
        return dataBaseHelper;
    }
}
