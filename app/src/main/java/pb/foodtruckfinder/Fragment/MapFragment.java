package pb.foodtruckfinder.Fragment;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pb.foodtruckfinder.R;


public class MapFragment extends android.support.v4.app.Fragment {

    private final static String TAG = MapFragment.class.getName();

    private MapView mMapView;
    private GoogleMap map;
    private Bundle mBundle;

    private List<LatLng> routePoints;

    private final float defaultZoomLevel = 14;

    private HashMap<String, LatLng> mTrucks = new HashMap<String, LatLng>() {
        {

            put("Kantarellkungen", new LatLng(59.338738,18.056576));
            put("SOOK Streetfood", new LatLng(59.3283175, 18.0598587));
            put("Silvias",new LatLng(59.3410617,18.0529373));
            put("Foodtruck Odjuret",new LatLng(59.3325716, 18.068081));
            put("Curbside STHLM", new LatLng(59.3291642, 18.0644496));

        }
    };

    public HashMap<String, LatLng> getTrucks() {
        return mTrucks;
    }


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

    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        public MarkerInfoWindowAdapter() {
        }

        @Override
        public View getInfoWindow(Marker marker) {

            // Getting view from the layout file
            View v = getActivity().getLayoutInflater().inflate(R.layout.infowindow_layout, null);
            return v;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

    public void setCanInteract(boolean canInteract) {
        if(map != null)
            map.getUiSettings().setScrollGesturesEnabled(canInteract);
    }


    private void setUpMap() {

        routePoints = new ArrayList<>();
        map.setMyLocationEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setInfoWindowAdapter(new MarkerInfoWindowAdapter());

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
    }


    public void updateMarker(Location location) {

        LatLng point = new LatLng(location.getLatitude(), location.getLongitude());

        if(routePoints.isEmpty())
            panToPosition(point, defaultZoomLevel);
        else
            panToPosition(point);

        routePoints.add(point);

        Polyline route = map.addPolyline(new PolylineOptions()
                .width(4)
                .color(Color.BLUE)
                .geodesic(false)
                .zIndex(0));
        route.setPoints(routePoints);


    }

    public void panToPosition(LatLng point) {
        float zoom;
        if(map == null)
            zoom = defaultZoomLevel;
        else
            zoom = map.getCameraPosition().zoom;

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoom));
        map.animateCamera(CameraUpdateFactory.zoomTo(zoom), 500, null);
    }

    public void panToPosition(LatLng point,float zoom) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoom+5));
        map.animateCamera(CameraUpdateFactory.zoomTo(zoom), 500, null);
    }

    @Override
    public void onStop() {
        super.onStop();
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
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

}