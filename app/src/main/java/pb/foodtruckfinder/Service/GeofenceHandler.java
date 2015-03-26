package pb.foodtruckfinder.Service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

import pb.foodtruckfinder.R;


/**
 * Created by hugo on 26/03/15.
 */
public class GeofenceHandler extends IntentService {

    public static final String TAG = "GeofenceHandler";
    public static final String GEO_PARCEL = "GeofenceHandlerLocation";

    public GeofenceHandler() {
        super(TAG);
    }


    private String errorCodeToString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "Geofence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "To many geofences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "To many pending geofence intents";
            default:
                return "Unknown error";
        }
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "Entered";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "Exited";
            default:
                return "Unkown";
        }
    }

    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "Error: " + errorCodeToString(geofencingEvent.getErrorCode()));
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );

            Log.i(TAG, geofenceTransitionDetails);

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(TAG);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(GEO_PARCEL, geofenceTransitionDetails);
            sendBroadcast(broadcastIntent);

            Log.d(TAG, "Sent broadcast!");

        } else {
            // Log the error.
            Log.e(TAG, "Invalid transistion type:" + geofenceTransition);
        }
    }
}
