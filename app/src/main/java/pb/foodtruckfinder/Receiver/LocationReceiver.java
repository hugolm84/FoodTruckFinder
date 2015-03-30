package pb.foodtruckfinder.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.ConcurrentLinkedQueue;

import pb.foodtruckfinder.Service.GeofenceHandler;
import pb.foodtruckfinder.Service.LocationHandler;
import pb.foodtruckfinder.Service.LocationService;

/**
 * Created by hugo on 26/03/15.
 */
public class LocationReceiver extends BroadcastReceiver {

    private final static String TAG = "LocationReciever";

    private ConcurrentLinkedQueue<Location> mOnPauseLocations = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Location> mOnPauseRemoteLocations = new ConcurrentLinkedQueue<>();

    private LocationReceiverCallback mCallback;
    private boolean mIsPaused;

    public interface LocationReceiverCallback {
        void onRemoteLocationReceived(final Location location, final String id);
        void onLocationReceived(final Location location);
        void onGeofenceReceived(final String geoString);
    }

    public void setReceiverCallback(LocationReceiverCallback callback) {
        mCallback = callback;
    }

    public void onPause() {
        mIsPaused = true;
    }

    public void onResume() {
        mIsPaused = false;
    }

    public Location getNextOnPausedLocation() {
        if(mOnPauseLocations.isEmpty())
            return null;
        return mOnPauseLocations.remove();
    }

    public Location getNextOnPausedRemoteLocation() {
        if(mOnPauseRemoteLocations.isEmpty())
            return null;
        return mOnPauseRemoteLocations.remove();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Got intent " + intent.getAction());

        if(intent.getAction().equals(LocationHandler.TAG)) {
            Location location = intent.getParcelableExtra(LocationHandler.LOC_PARCEL);
            if (location != null) {
                if (mIsPaused) {
                    Log.d(TAG, "Storing onPause location! Got:" + mOnPauseLocations.size());
                    mOnPauseLocations.add(location);
                } else {
                    mCallback.onLocationReceived(location);
                }
            }
        }

        if(intent.getAction().equals(GeofenceHandler.TAG)) {
            final String geoString = intent.getStringExtra(GeofenceHandler.GEO_PARCEL);
            if(geoString != null && !geoString.isEmpty())
                mCallback.onGeofenceReceived(geoString);
        }

        if(intent.getAction().equals(LocationService.TAG)) {
            Location location = intent.getParcelableExtra(LocationService.REMOTE_LOC);
            String id = intent.getStringExtra(LocationService.REMOTE_LOC_ID);
            if (location != null) {

                if(mIsPaused) {
                    Log.d(TAG, "Storing onPause RemoteLocation! Got:" + mOnPauseRemoteLocations.size());
                    Bundle locationBundle = new Bundle();
                    locationBundle.putString(LocationService.REMOTE_LOC_ID, id);
                    location.setExtras(locationBundle);
                    mOnPauseRemoteLocations.add(location);
                }
                else {
                    mCallback.onRemoteLocationReceived(location, id);
                }
            }
        }
    }
}
