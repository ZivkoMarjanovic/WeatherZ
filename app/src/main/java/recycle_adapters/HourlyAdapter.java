package recycle_adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zivko.weather.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import model_daily.Daily;
import model_forecast.Forecast;
import model_forecast.List;

/**
 * Created by Živko on 2017-01-05.
 */

public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.MyVH> {

    Context context;
    Forecast forecast;
    java.util.List<List> myForecastList = new ArrayList<List>();
    int daily_dt;

    public HourlyAdapter(Forecast forecast,int dt) {
        this.forecast = forecast;
        this.daily_dt = dt;

        int startOfDay = daily_dt - (11 * 60 * 60);
        int endOfDay = daily_dt + (13 * 60 * 60);

        for (int i = 0; i < forecast.getList().size(); i++) {
            if (forecast.getList().get(i).getDt() >= startOfDay && forecast.getList().get(i).getDt() < endOfDay)
                myForecastList.add(forecast.getList().get(i));
        }
        }


    public void updateDaily(Daily newDaily, int dt) {
        this.forecast = forecast;
        this.daily_dt = dt;
        this.notifyDataSetChanged();
    }

    @Override
    public MyVH onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.daily_single_row, parent, false);
        MyVH myHolder = new MyVH(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(MyVH holder, int i) {
        int t = myForecastList.get(i).getDt();
        long time = ((long) t) * 1000;
        Date p = new Date(time);
        holder.days.setText(new SimpleDateFormat("hh aa", Locale.getDefault()).format(p));

        String icon_id = myForecastList.get(i).getWeather().get(0).getIcon();
        Picasso.with(context)
                .load("http://openweathermap.org/img/w/" + icon_id + ".png")
                .into(holder.icon);

        if (myForecastList.get(i).getRain() != null && myForecastList.get(i).getRain().get3h() != null && !myForecastList.get(i).getRain().get3h().isNaN()) {
            holder.rain.setText(String.format(Locale.ENGLISH, "%.0f", myForecastList.get(i).getRain().get3h()));
        } else {
            holder.rain.setText("0 ");
        }
        holder.wind.setText(String.format(Locale.ENGLISH, "%.0f", myForecastList.get(i).getWind().getSpeed()));

        holder.degrees.setText(String.format(Locale.ENGLISH, "%.0f", myForecastList.get(i).getMain().getTemp() - 273.15)+ " \u00B0");

        holder.degrees2.setText("");


    }

    @Override
    public int getItemCount() {
        return myForecastList.size();
    }

    public class MyVH extends RecyclerView.ViewHolder {
        TextView days;
        ImageView icon;
        TextView rain;
        TextView wind;
        TextView degrees;
        TextView degrees2;


        public MyVH(View itemView) {
            super(itemView);
            days = (TextView) itemView.findViewById(R.id.days);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            rain = (TextView) itemView.findViewById(R.id.rain);
            wind = (TextView) itemView.findViewById(R.id.wind);
            degrees = (TextView) itemView.findViewById(R.id.degrees);
            degrees2 = (TextView) itemView.findViewById(R.id.degrees2);
        }
    }

}
