package pb.foodtruckfinder.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import pb.foodtruckfinder.Service.GeofenceHandler;
import pb.foodtruckfinder.Service.LocationHandler;

/**
 * Created by hugo on 26/03/15.
 */
public class LocationReceiver extends BroadcastReceiver {

    private final static String TAG = "LocationReciever";

    private LocationReceiverCallback mCallback;

    public interface LocationReceiverCallback {
        void onLocationReceived(final Location location);
        void onGeofenceReceived(final String geoString);
    }

    public void setReceiverCallback(LocationReceiverCallback callback) {
        mCallback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Got intent " + intent.getAction());

        if(intent.getAction().equals(LocationHandler.TAG)) {
            Location location = intent.getParcelableExtra(LocationHandler.LOC_PARCEL);
            if (location != null) {
                mCallback.onLocationReceived(location);
            }
        }

        if(intent.getAction().equals(GeofenceHandler.TAG)) {
            final String geoString = intent.getStringExtra(GeofenceHandler.GEO_PARCEL);
            if(geoString != null && !geoString.isEmpty())
                mCallback.onGeofenceReceived(geoString);
        }
    }
}
