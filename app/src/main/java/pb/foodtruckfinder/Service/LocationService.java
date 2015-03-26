package pb.foodtruckfinder.Service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hugo on 26/03/15.
 */
public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {

    private final static String TAG = "LocationService";
    private final IBinder mBinder = new LocalBinder();

    private ServiceCallback mServiceCallback;
    private List<Geofence> mGeofences = new ArrayList<>();

    protected final static long     FASTEST_INTERVAL = 5000L;
    protected final static long     INTERVAL = 10000L;
    protected final static float    SMALLEST_DISPLACEMENT = 75.0F;
    protected final static float    GEOFENCE_RADIUS = 100f;
    protected final static long     GEOFENCE_EXPIRATION_IN_HOURS = 12;
    protected static final long     GEOFENCE_EXPIRATION_IN_MILLISECONDS = GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;

    protected GoogleApiClient mGoogleClient;

    public void setServiceCallback(ServiceCallback callback) {
        mServiceCallback = callback;
    }

    public static interface ServiceCallback {
        void onConnected();
    }

    @Override public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "OnCreate");
        // you can also add more APIs and scopes here
        mGoogleClient = new GoogleApiClient.Builder(this, this, this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mGoogleClient.connect();
        return START_STICKY;
    }

    @Override
    public void onConnected(Bundle bundle) {

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setFastestInterval(FASTEST_INTERVAL)
                .setInterval(INTERVAL)
                .setSmallestDisplacement(SMALLEST_DISPLACEMENT);

        PendingIntent pendingIntent = PendingIntent.getService(this, 0,
                new Intent(this, LocationHandler.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleClient, locationRequest, pendingIntent);

        if(mServiceCallback != null)
            mServiceCallback.onConnected();

    }

    public Geofence createGeofence(Map.Entry<String, LatLng> entry) {

        return new Geofence.Builder()
                .setRequestId(entry.getKey())
                .setCircularRegion(entry.getValue().latitude, entry.getValue().longitude, GEOFENCE_RADIUS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .build();
    }

    public void setupGeofence(HashMap<String, LatLng> geofences) {

        if(!mGeofences.isEmpty()) {
            /**
             * Clear previous fences?
             */
            return;
        }

        for(Map.Entry<String, LatLng> entry : geofences.entrySet()) {
            mGeofences.add(createGeofence(entry));
        }

        PendingIntent intent = PendingIntent.getService(this, 0,
                new Intent(this, GeofenceHandler.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        LocationServices.GeofencingApi.addGeofences(mGoogleClient, mGeofences, intent)
                .setResultCallback(this);

    }

    @Override
    public void onResult(Status status) {

        if (status.isSuccess()) {
            Log.d(TAG, "Successfully added geofences");
        } else {
            Log.d(TAG, "Failed to add geofences " + status.toString());
        }
    }


    @Override
    public void onDestroy() {

        mGoogleClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended!" + i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed!" + connectionResult.toString());

    }
}
