package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;



/**
 * Created by Živko on 2016-11-17.
 */

public class DataBaseHelper extends OrmLiteSqliteOpenHelper {

    public static final String BASENAME = "weatherz.db";
    public static final int VERSION = 1;

    Dao<MyLocation, Integer> myLocationDao = null;

    public DataBaseHelper(Context context) {
        super(context, BASENAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {

        try {
            TableUtils.createTable(connectionSource, MyLocation.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, MyLocation.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public Dao<MyLocation, Integer> getMyLocationDao() throws SQLException {
        if (myLocationDao == null) {
            myLocationDao = getDao(MyLocation.class);
        }

        return myLocationDao;
    }

    @Override
    public void close() {

        myLocationDao = null;

        super.close();
    }
}
