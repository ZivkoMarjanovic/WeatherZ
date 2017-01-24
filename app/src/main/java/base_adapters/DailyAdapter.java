package base_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.zivko.weather.R;
import com.squareup.picasso.Picasso;

import java.util.Locale;


import model_daily.Daily;

/**
 * Created by Živko on 2016-11-28.
 */

public class DailyAdapter extends BaseAdapter {

    Context context;
    Daily daily;
    String[] day = new String[6];

    public DailyAdapter(Context context, Daily daily) {
        this.context = context;
        this.daily = daily;
        day = context.getResources().getStringArray(R.array.next_day);
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
        setDailyDates(row, i);
        return row;
    }

    private void setDailyDates(final View row, int i) {

        final TextView days = (TextView) row.findViewById(R.id.days);
        days.setText(day[i]);

        final ImageView icon = (ImageView) row.findViewById(R.id.icon);
        String icon_id = daily.getList().get(i).getWeather().get(0).getIcon();
        Picasso.with(context)
                .load("http://openweathermap.org/img/w/"+icon_id+".png")
                .into(icon);

        final TextView rain = (TextView) row.findViewById(R.id.rain);
        if (daily.getList().get(i).getRain() != null && !daily.getList().get(i).getRain().isNaN()) {
            rain.setText(String.format(Locale.ENGLISH, "%.0f", daily.getList().get(i).getRain()));
        } else {
            rain.setText("0.00");
        }

        final TextView wind = (TextView) row.findViewById(R.id.wind);
        wind.setText(String.format(Locale.ENGLISH, "%.0f", daily.getList().get(i).getSpeed()));

        final TextView degrees = (TextView) row.findViewById(R.id.degrees);
        degrees.setText(String.format(Locale.ENGLISH, "%.0f", daily.getList().get(i).getTemp().getMax()-273.15));

        final TextView degrees2 = (TextView) row.findViewById(R.id.degrees2);
        degrees2.setText(String.format(Locale.ENGLISH, "%.0f", daily.getList().get(i).getTemp().getMin()-273.15));


    }

    public void refreshDailyAdapter(Daily daily) {
        this.daily = daily;
        this.notifyDataSetChanged();
    }
}
