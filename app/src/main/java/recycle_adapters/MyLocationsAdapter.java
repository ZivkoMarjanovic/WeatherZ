package recycle_adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zivko.weather.R;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.squareup.picasso.Picasso;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import db.DataBaseHelper;
import db.MyLocation;
import model_daily.Daily;

/**
 * Created by Živko on 2017-01-05.
 */

public class MyLocationsAdapter extends RecyclerView.Adapter<MyLocationsAdapter.MyVH> {

    MyLocationsOnItemClickListener myOnItemClickListener;
    Context context;
    DataBaseHelper dataBaseHelper;
    List<MyLocation> myLocationList;

    public MyLocationsAdapter(MyLocationsOnItemClickListener myOnItemClickListener) {
        this.myOnItemClickListener = myOnItemClickListener;
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

    public interface MyLocationsOnItemClickListener {
        void MyLocationsOnClick(int position,  List<MyLocation> myLocationList);
    }

    public void refreshMyLocationsAdapter(List<MyLocation> myLocations1) {
        myLocationList = myLocations1;
        notifyDataSetChanged();
    }

    @Override
    public MyVH onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.location_list, parent, false);
        MyVH myHolder = new MyVH(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(MyVH holder, int i) {
        holder.days.setText(myLocationList.get(i).getName());
        holder.checkBox.setChecked(true);
    }

    @Override
    public int getItemCount() {
        return myLocationList.size();
    }

    public class MyVH extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener {
        TextView days;
        CheckBox checkBox;

        public MyVH(View itemView) {
            super(itemView);
            days = (TextView) itemView.findViewById(R.id.textList);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);

            checkBox.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            myOnItemClickListener.MyLocationsOnClick(position, myLocationList);
        }
    }

    public DataBaseHelper getDatabaseHelper() {
        if (dataBaseHelper == null) {
            dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
        }
        return dataBaseHelper;
    }
}
