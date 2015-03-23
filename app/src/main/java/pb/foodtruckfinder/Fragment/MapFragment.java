package pb.foodtruckfinder.Fragment;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.*;

import java.util.HashMap;
import java.util.Map;

import pb.foodtruckfinder.R;


public class MapFragment extends android.support.v4.app.Fragment {

    private MapView mMapView;
    private GoogleMap map;
    private Bundle mBundle;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Marker currentMarker;

    // Stockholm is the default location.
    static final LatLng STOCKHOLM = new LatLng(59.338092,18.069656);

    private HashMap<String, LatLng> mTrucks = new HashMap<String, LatLng>() {
        {
            put("Kantarellkungen", new LatLng(59.345061, 18.063060));
            put("SOOK Streetfood", new LatLng(59.346538, 18.073317));
            put("Silvias",new LatLng(59.332818, 18.073580));
            put("Foodtruck Odjuret",new LatLng(59.343947, 18.055620));
            put("Curbside STHLM", new LatLng(59.343589, 18.063291));
        }
    };

    private final static String TAG = MapFragment.class.getName();
    private final float defaultZoomLevel = 14;


    protected static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MapFragment newInstance(int sectionNumber) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MapFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_map, container, false);

        MapsInitializer.initialize(getActivity());

        mMapView = (MapView) inflatedView.findViewById(R.id.map);
        mMapView.onCreate(mBundle);
        setUpMapIfNeeded(inflatedView);

        return inflatedView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;
    }

    private void setUpMapIfNeeded(View inflatedView) {
        if (map == null) {
            map = ((MapView) inflatedView.findViewById(R.id.map)).getMap();
            if (map != null) {
                setUpMap();
            }
        }
    }

    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
    {
        public MarkerInfoWindowAdapter()
        {
        }

        @Override
        public View getInfoWindow(Marker marker)
        {

            // Getting view from the layout file
            View v = getActivity().getLayoutInflater().inflate(R.layout.infowindow_layout, null);
            return v;
            /*TextView title = (TextView) v.findViewById(R.id.title);
            title.setText(marker.getTitle());

            TextView address = (TextView) v.findViewById(R.id.distance);
            address.setText(marker.getSnippet());*/
            //return null;
        }

        @Override
        public View getInfoContents(Marker marker)
        {
            return null;//View v  = getActivity().getLayoutInflater().inflate(R.layout.infowindow_layout, null);
            //return v;
        }
    }

    private void setUpMap() {

        map.setMyLocationEnabled(true);
        map.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
        setCurrentLocation();

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_local_shipping_black_48dp);

        for(Map.Entry<String, LatLng> entry : mTrucks.entrySet()) {
            map.addMarker(new MarkerOptions()
                    .position(entry.getValue())
                    .title(entry.getKey())
                    .icon(icon)
                    .anchor(0.0f, 1.0f)
                    .snippet("TEST SNIPPET")
                    .draggable(false));
        }

        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        try {

            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,locationListener);

            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        } catch( IllegalArgumentException e) {
            Log.d(TAG, "No providers available");
            e.printStackTrace();
        }

    }


    public void updateMarker(LatLng point) {

        if( currentMarker != null ) {
            currentMarker.remove();
        }

        currentMarker = map.addMarker( new MarkerOptions()
                        .position(point)
                        .title("Min plats")
                        .draggable(true)
        );


    }

    private LatLng getCurrentMarkerPosition() {
        if( currentMarker != null)
            return currentMarker.getPosition();
        return new LatLng(0,0);
    }

    private LatLng getCurrentPosition() {
        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(lastLocation == null)
            lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(lastLocation == null)
            return STOCKHOLM;

        double lat = lastLocation.getLatitude();
        double lng = lastLocation.getLongitude();
        return new LatLng(lat, lng);
    }

    private void panToPosition(LatLng point) {
        float zoom;
        if(map == null)
            zoom = defaultZoomLevel;
        else
            zoom = map.getCameraPosition().zoom;

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoom));
        map.animateCamera(CameraUpdateFactory.zoomTo(zoom), 500, null);
    }

    private void panToPosition(LatLng point,float zoom) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoom+5));
        map.animateCamera(CameraUpdateFactory.zoomTo(zoom), 500, null);
    }

    private void setCurrentLocation() {
        LatLng lastLatLng = getCurrentPosition();
        updateMarker(lastLatLng);
        panToPosition(lastLatLng, defaultZoomLevel);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(map != null ) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        if(map != null){
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

}