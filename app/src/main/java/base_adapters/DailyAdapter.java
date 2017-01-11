package base_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.zivko.weather.R;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import net.Setvice;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


import model_daily.Daily;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.R.id.list;
import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by Živko on 2016-11-28.
 */

public class DailyAdapter extends BaseAdapter {

    Context context;
    Daily daily;
    String[] day = {"Next day", "In two days", "In three days", "In four days", "In five days"};

    public DailyAdapter(Context context, Daily daily) {
        this.context = context;
        this.daily = daily;

    }

    @Override
    public int getCount() {
        return daily.getCnt();
    }

    @Override
    public Object getItem(int i) {
        return daily.getList().get(i);
    }

    @Override
    public long getItemId(int i) {
        return daily.getList().get(i).getDt();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.daily_single_row, viewGroup, false);
        getArtistByName(row, i);
        return row;
    }

    private void getArtistByName(final View row, int i) {


        final TextView days = (TextView) row.findViewById(R.id.days);
        days.setText(day[i]);

        final ImageView icon = (ImageView) row.findViewById(R.id.icon);
        String icon_id = daily.getList().get(i).getWeather().get(0).getIcon();
        // icon.setImageDrawable();

        final TextView rain = (TextView) row.findViewById(R.id.rain);
        rain.setText(String.format(Locale.CANADA, "%.2f", daily.getList().get(i).getRain()));

        final TextView wind = (TextView) row.findViewById(R.id.wind);
        wind.setText(String.format(Locale.CANADA, "%.2f", daily.getList().get(i).getSpeed()));

        final TextView degrees = (TextView) row.findViewById(R.id.degrees);
        degrees.setText(String.format(Locale.CANADA, "%.2f", daily.getList().get(i).getTemp().getMax()-273.15));

        final TextView degrees2 = (TextView) row.findViewById(R.id.degrees2);
        degrees2.setText(String.format(Locale.CANADA, "%.2f", daily.getList().get(i).getTemp().getMin()-273.15));


    }


   /* public void refreshAdapter() {
        try {
            list = getDatabaseHelper().getShoppingListDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (dataBaseHelper != null) {
            OpenHelperManager.releaseHelper();
            dataBaseHelper = null;
        }
        this.notifyDataSetChanged();
    }

    public DataBaseHelper getDatabaseHelper() {
        if (dataBaseHelper == null) {
            dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
        }
        return dataBaseHelper;
    }
*/

}
