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
import java.util.List;
import java.util.Locale;

import model_daily.Daily;

/**
 * Created by Živko on 2017-01-05.
 */

public class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.MyVH> {

    Daily daily = new Daily();
    DailyOnItemClickListener myOnItemClickListener;
    Context context;
    String[] day = new String[6];

    public DailyAdapter(Daily daily, DailyOnItemClickListener myOnItemClickListener) {
        this.myOnItemClickListener = myOnItemClickListener;
        this.daily = daily;
        }

    public interface DailyOnItemClickListener {
        void DailyOnClick(View v, int position, Daily daily);
    }

    public void updateDaily(Daily newDaily) {
        this.daily = newDaily;
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
    public void onBindViewHolder(MyVH holder, int position) {
        //day = context.getResources().getStringArray(R.array.next_day);
        int dt = daily.getList().get(position).getDt();
        long longDT = (long) dt;
        Date p = new Date(longDT * 1000);
        holder.days.setText(new SimpleDateFormat("EEEE", Locale.getDefault()).format(p));

        //holder.days.setText(day[position]);
        String icon_id = daily.getList().get(position).getWeather().get(0).getIcon();
        Picasso.with(context)
                .load("http://openweathermap.org/img/w/" + icon_id + ".png")
                .into(holder.icon);

        if (daily.getList().get(position).getRain() != null && !daily.getList().get(position).getRain().isNaN()) {
            holder.rain.setText(String.format(Locale.ENGLISH, "%.0f", daily.getList().get(position).getRain()));
        } else {
            holder.rain.setText("0 ");
        }

        if (daily.getList().get(position).getSpeed()!= null && !daily.getList().get(position).getSpeed().isNaN()) {
            holder.wind.setText(String.format(Locale.ENGLISH, "%.0f", daily.getList().get(position).getSpeed()));
        }

        if (daily.getList().get(position).getTemp().getMax()!= null && !daily.getList().get(position).getTemp().getMax().isNaN()) {
            holder.degrees.setText(String.format(Locale.ENGLISH, "%.0f", daily.getList().get(position).getTemp().getMax() - 273.15)+ " \u00B0");
        }

        if (daily.getList().get(position).getTemp().getMin()!= null && !daily.getList().get(position).getTemp().getMin().isNaN()) {
            holder.degrees2.setText(String.format(Locale.ENGLISH, "%.0f", daily.getList().get(position).getTemp().getMin() - 273.15)+ " \u00B0");
        }

    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public class MyVH extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener {
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
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            myOnItemClickListener.DailyOnClick(view, position, daily);
        }
    }
}
