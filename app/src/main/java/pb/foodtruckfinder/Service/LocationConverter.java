package pb.foodtruckfinder.Service;

import android.location.Location;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;
import com.cocoahero.android.geojson.Point;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hugo on 30/03/15.
 */
public class LocationConverter {

    public static final String TIME         = "time";
    public static final String SPEED        = "speed";
    public static final String ELAPSED      = "elapsed";
    public static final String BEARING      = "bearing";
    public static final String ACCURACY     = "accuracy";

    public static JSONObject locationToJSON(android.location.Location location) throws JSONException {

        Point point = new Point(location.getLatitude(), location.getLongitude(), location.getAltitude());

        Feature feature = new Feature(point);
        feature.setIdentifier(location.getProvider());

        JSONObject properties = new JSONObject();
        properties.put(SPEED, location.getSpeed());
        properties.put(TIME, location.getTime());
        properties.put(BEARING, location.getBearing());
        properties.put(ACCURACY, location.getAccuracy());
        properties.put(ELAPSED, location.getElapsedRealtimeNanos());
        feature.setProperties(properties);
        return feature.toJSON();
    }

    public static Location featureToLocation(Feature feature) throws JSONException {

        JSONObject prop = feature.getProperties();
        JSONObject geo = feature.getGeometry().toJSON();

        Point point = new Point(geo);

        Location location = new Location(feature.getIdentifier());
        location.setLongitude(point.getPosition().getLongitude());
        location.setLatitude(point.getPosition().getLatitude());
        location.setAccuracy(new Double(prop.getDouble(ACCURACY)).floatValue());
        location.setSpeed(new Double(prop.getDouble(SPEED)).floatValue());
        location.setBearing(new Double(prop.getDouble(BEARING)).floatValue());
        location.setTime(new Double(prop.getDouble(TIME)).longValue());
        location.setElapsedRealtimeNanos(new Double(prop.getDouble(ELAPSED)).longValue());

        return location;
    }
}
