package base_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zivko.weather.R;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import model_forecast.Forecast;

/**
 * Created by Živko on 2016-11-28.
 */

public class HourlyAdapter extends BaseAdapter {

    Context context;
    Forecast forecast;
    String[] day = {"Next day", "In two days", "In three days", "In four days", "In five days"};
    List<model_forecast.List> myForecastList = new ArrayList<model_forecast.List>();
    int daily_dt;

    public HourlyAdapter(Context context, Forecast forecast, int dt) {
        this.context = context;
        this.forecast = forecast;
        this.daily_dt = dt;

        /*Instant instant = Instant.ofEpochSecond(daily_dt);
        LocalDateTime localDateTime = LocalDate.from(instant).atStartOfDay();
        Instant instant1localDateTime.toInstant(ZoneOffset.UTC));;
        //java.util.Date date = localDateTime.toEpochSecond();*/
        int startOfDay = daily_dt - (10*60*60);
        int endOfDay= daily_dt + (14*60*60);


        for (int i=0; i<forecast.getList().size();i++)
        {
           if (forecast.getList().get(i).getDt()>=startOfDay && forecast.getList().get(i).getDt()< endOfDay)
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
        SimpleDateFormat formatter = new SimpleDateFormat("hh: aa");
        String dateString = formatter.format(new Date(myForecastList.get(i).getDt()*1000));
        days.setText(dateString);

        final ImageView icon = (ImageView) row.findViewById(R.id.icon);
        String icon_id = myForecastList.get(i).getWeather().get(0).getIcon();
        // icon.setImageDrawable();

        final TextView rain = (TextView) row.findViewById(R.id.rain);
        if (myForecastList.get(i).getRain()!= null) {
            rain.setText(String.format(Locale.CANADA, "%.2f", myForecastList.get(i).getRain().get3h()));
        } else {rain.setText("0.00");}
        final TextView wind = (TextView) row.findViewById(R.id.wind);
        wind.setText(String.format(Locale.CANADA, "%.2f", myForecastList.get(i).getWind().getSpeed()));

        final TextView degrees = (TextView) row.findViewById(R.id.degrees);
        degrees.setText(String.format(Locale.CANADA, "%.2f", myForecastList.get(i).getMain().getTempMax()-273.15));

        final TextView degrees2 = (TextView) row.findViewById(R.id.degrees2);
        degrees2.setText(String.format(Locale.CANADA, "%.2f", myForecastList.get(i).getMain().getTempMin()-273.15));


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
