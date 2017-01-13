package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import static db.MyLocation.TABLE_NAME;


/**
 * Created by Živko on 2016-10-25.
 */
@DatabaseTable(tableName = TABLE_NAME)
public class MyLocation {

    public static final String TABLE_NAME = "mylocation";
    public static final String ID_LOCATION = "id";
    public static final String NAME = "name";
    public static final String LAT = "lat";
    public static final String LON = "lon";


    @DatabaseField(generatedId = true, columnName = ID_LOCATION, unique = true)
    int id;

    @DatabaseField(columnName = NAME)
    String name;

    @DatabaseField(columnName = LAT)
    double lat;

    @DatabaseField(columnName = LON)
    double lon;

    public MyLocation() {
    }

    public MyLocation(String name, double lat, double lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "MyLocation{" +
                "name='" + name + '\'' +
                '}';
    }
}
