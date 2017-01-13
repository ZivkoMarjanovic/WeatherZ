package intent_service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Živko on 2016-12-22.
 */

public class FetchAddressIntentService extends IntentService {

    protected ResultReceiver mReceiver;
    double lat;
    double lon;

    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString("result", message);
        bundle.putDouble("lat", lat);
        bundle.putDouble("lon", lon);
        mReceiver.send(resultCode, bundle);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mReceiver = intent.getParcelableExtra("receiverTag");

        lat = intent.getDoubleExtra("lat", 0.00);
        lon = intent.getDoubleExtra("lon", 0.00);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        int code = 0;
        String myName = null;
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses.size() > 0) {
                String myAddress = addresses.get(0).getAddressLine(1);
                String myStreet = addresses.get(0).getThoroughfare();
                myName = myAddress + ", " + myStreet;
                code = 1;
            } else {
                myName = "Could not find the address";
                code = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (myName==null) myName = "Could not find the address";
        }

        deliverResultToReceiver(code, myName);

    }
}
