package pb.foodtruckfinder.Service;

import android.content.Context;
import android.location.Location;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

/**
 * Created by hugo on 26/03/15.
 */
public class MockRoute {

    private static final String TAG = "MockRoute";
    private Context context;
    private LinkedList<Location> mLocations = new LinkedList<>();

    public MockRoute(Context ctx) {

        context = ctx;
        loadJSONFromAsset();
    }


    public void loadJSONFromAsset() {
        try {

            InputStream is = context.getAssets().open("route.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            final String json = new String(buffer, "UTF-8");

            JSONObject obj = new JSONObject(json);
            JSONArray coords = obj.getJSONArray("coordinates");
            for(int i = 0; i < coords.length(); i++) {
                JSONArray row = coords.getJSONArray(i);
                Double lng = Double.valueOf(row.getString(0));
                Double lat = Double.valueOf(row.getString(1));

                //Log.d(TAG, "Lng "+Double.valueOf(row.getString(0)) + "==" + row.getString(0));
                //Log.d(TAG, "Lat "+Double.valueOf(row.getString(1)) + "==" + row.getString(1));

                Location location = new Location("mock");
                location.setLatitude(lat);
                location.setLongitude(lng);
                location.setAccuracy(4);
                mLocations.add(location);

            }

        } catch (IOException | JSONException ex) {
            ex.printStackTrace();
        }
    }

    public Location getNextCoord() {
        if(!mLocations.isEmpty())
            return mLocations.removeFirst();
        return null;
    }
}
