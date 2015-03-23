package pb.foodtruckfinder.Adapter;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.widget.Adapter;

import com.google.android.gms.maps.model.LatLng;
import com.mopub.nativeads.MoPubAdAdapter;
import com.mopub.nativeads.MoPubNativeAdRenderer;
import com.mopub.nativeads.RequestParameters;
import com.mopub.nativeads.ViewBinder;

import java.util.List;

import pb.foodtruckfinder.R;

/**
 * Created by hugo on 20/03/15.
 */
public class MoPubAdapter extends MoPubAdAdapter {


    private static final String MOPUB_NATIVE_AD_UNIT_ID = "69fd5002cecb4646ab9b4a47325df211";
    private static final LatLng STOCKHOLM = new LatLng(59.338092,18.069656);
    private LocationManager locationManager;

    public MoPubAdapter(Context ctx, Adapter adapter) {
        super(ctx, adapter);

        ViewBinder viewBinder = new ViewBinder.Builder(R.layout.example_native_ad)
                .titleId(R.id.native_title)
                .textId(R.id.native_text)
                .mainImageId(R.id.native_main_image)
                .iconImageId(R.id.native_icon_image)
                .callToActionId(R.id.native_cta)
                .build();

        MoPubNativeAdRenderer adRenderer = new MoPubNativeAdRenderer(viewBinder);
        registerAdRenderer(adRenderer);

        locationManager = (LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
    }

    public void loadAds() {

        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(lastLocation == null)
            lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(lastLocation == null) {
            lastLocation = new Location("Stockholm");
            lastLocation.setLatitude(STOCKHOLM.latitude);
            lastLocation.setLongitude(STOCKHOLM.longitude);
        }

        RequestParameters parameters = new RequestParameters.Builder()
                //.keywords("your target words here")
                .location(lastLocation)
                .build();

        // Request ads when the user returns to this activity
        loadAds(MOPUB_NATIVE_AD_UNIT_ID, parameters);
    }

}
