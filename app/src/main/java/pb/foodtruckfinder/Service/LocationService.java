package pb.foodtruckfinder.Service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.GeoJSON;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pb.foodtruckfinder.Socket.IO.SocketIOStatic;

/**
 * Created by hugo on 26/03/15.
 */
public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<Status> {

    public final static String TAG = "LocationService";
    public final static String REMOTE_LOC = "RemoteLocation";
    public final static String REMOTE_LOC_ID = "RemoteLocationID";

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
    protected Socket mSocket;
    protected String uuid;


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

    public void onAuthed(final String authToken) {

        Log.d(TAG, "OnAuthed");

        try {

            mSocket = SocketIOStatic.socket(authToken);
            mSocket.on(Socket.EVENT_CONNECT_ERROR,      onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT,    onConnectError);

            // Local
            mSocket.on(SocketIOStatic.EVENT_SOCKET_CONNECTED, onConnected);
            mSocket.on(SocketIOStatic.EVENT_SOCKET_DISCONNECTED, onDisconnected);
            mSocket.on(SocketIOStatic.EVENT_SOCKET_DISCONNECT, onDisconnect);
            mSocket.on(SocketIOStatic.EVENT_SOCKET_MESSAGE, onNewMessage);
            mSocket.on(SocketIOStatic.EVENT_SOCKET_REMOTE_LOCATION, onRemoteLocation);

            mSocket.connect();

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
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

        PendingIntent.getService(this, 0,
                new Intent(this, LocationHandler.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleClient, locationRequest, this);

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
        Log.d(TAG, "Destroying service");
        if(mSocket != null && mSocket.connected())
            mSocket.disconnect();
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

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, "ConnectionError " + args[0].toString());
        }
    };

    private Emitter.Listener onConnected = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, "Connected " + args[0].toString());
            uuid = args[0].toString();
        }
    };

    private Emitter.Listener onDisconnected = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, "Disconnected " + args[0].toString());
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, "Disconnect " + args[0].toString());
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, "New message: " + args[0]);
        }
    };

    private Emitter.Listener onRemoteLocation = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            Log.d(TAG, "New remote location: " + args[0] + " -> " + args[1]);

            try {

                Feature feature = (Feature)GeoJSON.parse(args[0].toString());
                Location location = LocationConverter.featureToLocation(feature);
                broadcastLocation(location, args[1].toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed!");
        sendLocationToSocket(location);
        // DEBUG
        broadcastLocation(location, "NonConnected " + uuid);
    }

    private void broadcastLocation(Location location, final String uuid) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(TAG);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(REMOTE_LOC, location);
        broadcastIntent.putExtra(REMOTE_LOC_ID, uuid);
        sendBroadcast(broadcastIntent);
    }
    private void sendLocationToSocket(Location location) {
        if(mSocket != null) {
            try {

                JSONObject loc = LocationConverter.locationToJSON(location);
                mSocket.emit("location", loc, uuid);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            Log.d(TAG, "Failed to send location");
        }
    }
}
