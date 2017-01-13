package base_adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zivko.weather.R;
import com.squareup.picasso.Picasso;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import model_forecast.Forecast;

/**
 * Created by Živko on 2016-11-28.
 */

public class HourlyAdapter extends BaseAdapter {

    Context context;
    Forecast forecast;
    List<model_forecast.List> myForecastList = new ArrayList<model_forecast.List>();
    int daily_dt;

    public HourlyAdapter(Context context, Forecast forecast, int dt) {
        this.context = context;
        this.forecast = forecast;
        this.daily_dt = dt;

        int startOfDay = daily_dt - (11 * 60 * 60);
        int endOfDay = daily_dt + (13 * 60 * 60);

        for (int i = 0; i < forecast.getList().size(); i++) {
            if (forecast.getList().get(i).getDt() >= startOfDay && forecast.getList().get(i).getDt() < endOfDay)
                myForecastList.add(forecast.getList().get(i));
        }
    }

    @Override
    public int getCount() {
        return myForecastList.size();
    }

    @Override
    public Object getItem(int i) {
        return myForecastList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return myForecastList.get(i).getDt();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.daily_single_row, viewGroup, false);
        getHourlyRow(row, i);
        return row;
    }

    private void getHourlyRow(final View row, int i) {


        final TextView days = (TextView) row.findViewById(R.id.days);

        int t = myForecastList.get(i).getDt();
        long time = ((long) t) * 1000;
        Date p = new Date(time);
        days.setText(new SimpleDateFormat("hh aa", Locale.getDefault()).format(p));

        final ImageView icon = (ImageView) row.findViewById(R.id.icon);
        String icon_id = myForecastList.get(i).getWeather().get(0).getIcon();
        Picasso.with(context)
                .load("http://openweathermap.org/img/w/" + icon_id + ".png")
                .into(icon);

        final TextView rain = (TextView) row.findViewById(R.id.rain);
        if (myForecastList.get(i).getRain() != null && myForecastList.get(i).getRain().get3h() != null && !myForecastList.get(i).getRain().get3h().isNaN()) {
            rain.setText(String.format(Locale.getDefault(), "%.2f", myForecastList.get(i).getRain().get3h()));
        } else {
            rain.setText("0.00");
        }
        final TextView wind = (TextView) row.findViewById(R.id.wind);
        wind.setText(String.format(Locale.getDefault(), "%.2f", myForecastList.get(i).getWind().getSpeed()));

        final TextView degrees = (TextView) row.findViewById(R.id.degrees);
        degrees.setText(String.format(Locale.getDefault(), "%.2f", myForecastList.get(i).getMain().getTempMax() - 273.15));

        final TextView degrees2 = (TextView) row.findViewById(R.id.degrees2);
        degrees2.setText(String.format(Locale.getDefault(), "%.2f", myForecastList.get(i).getMain().getTempMin() - 273.15));

    }
}
