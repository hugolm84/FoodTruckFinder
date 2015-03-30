package pb.foodtruckfinder.Service;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import com.google.android.gms.location.LocationServices;

/**
 * Created by hugo on 26/03/15.
 */
public class MockLocationService extends LocationService {

    final Handler handler = new Handler();
    private MockRoute route;

    @Override
    public void onCreate() {
        super.onCreate();
        route = new MockRoute(getApplicationContext());
    }

    @Override
    public void onConnected(Bundle bundle) {

        LocationServices.FusedLocationApi.setMockMode(mGoogleClient, true);

        super.onConnected(bundle);

        final Runnable r = new Runnable() {
            public void run() {
                if(mGoogleClient.isConnected()) {

                    Location loc = route.getNextCoord();
                    if (loc != null) {
                        long currentTime = System.currentTimeMillis();
                        long elapsedTimeNanos = SystemClock.elapsedRealtimeNanos();
                        loc.setElapsedRealtimeNanos(elapsedTimeNanos);
                        loc.setTime(currentTime);
                        LocationServices.FusedLocationApi.setMockLocation(mGoogleClient, loc);
                        handler.postDelayed(this, FASTEST_INTERVAL);
                    }
                }
            }
        };

        handler.postDelayed(r, 1000);
    }
}
