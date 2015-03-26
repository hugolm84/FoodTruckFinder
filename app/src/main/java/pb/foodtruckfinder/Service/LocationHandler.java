package pb.foodtruckfinder.Service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderApi;


/**
 * Created by hugo on 26/03/15.
 */
public class LocationHandler extends IntentService {

    public static final String TAG = "LocationHandler";
    public static final String LOC_PARCEL = "LocationHandlerLocation";

    private Location mLastLocation;

    public LocationHandler() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final Location location = intent.getParcelableExtra(FusedLocationProviderApi.KEY_LOCATION_CHANGED);

        if(mLastLocation != null) {
            if(location.getLatitude() == mLastLocation.getLatitude()
                    && location.getLongitude() == mLastLocation.getLongitude()) {
                return;
            }
        }

        mLastLocation = location;

        Log.d(TAG, "Got Location: " + location);
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(TAG);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(LOC_PARCEL, location);
        sendBroadcast(broadcastIntent);
    }
}
