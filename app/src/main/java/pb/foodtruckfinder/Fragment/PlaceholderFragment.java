package pb.foodtruckfinder.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.mopub.nativeads.MoPubAdAdapter;
import com.mopub.nativeads.MoPubNativeAdRenderer;
import com.mopub.nativeads.RequestParameters;
import com.mopub.nativeads.ViewBinder;
import java.util.ArrayList;

import pb.foodtruckfinder.R;

/**
 * Created by hugo on 20/03/15.
 */

public class PlaceholderFragment extends Fragment {


    protected static final String ARG_SECTION_NUMBER = "section_number";
    private TwitterLoginButton loginButton;

    // TODO: Replace this test id with your personal ad unit id
    private static final String MOPUB_NATIVE_AD_UNIT_ID = "6eaafa8a1f9d44d2961112d17f3fd168";
    private MoPubAdAdapter adAdapter;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    public PlaceholderFragment() {
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("PlaceHolder", "OnActivityResult");
        loginButton.onActivityResult(requestCode, resultCode,
                data);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Optional targeting parameters
        RequestParameters parameters = new RequestParameters.Builder()
                //.keywords("your target words here")
                .build();

        // Request ads when the user returns to this activity
        adAdapter.loadAds(MOPUB_NATIVE_AD_UNIT_ID, parameters);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adAdapter.destroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView sampleListView = (ListView) rootView.findViewById(R.id.mopub_sample_list_view);
        ArrayList<String> sampleItems = new ArrayList<String>();
        for (int i = 1; i <= 20; i++) {
            sampleItems.add("Item " + i);
        }
        ArrayAdapter<String> sampleAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                sampleItems
        );
        ViewBinder viewBinder = new ViewBinder.Builder(R.layout.example_native_ad)
                .titleId(R.id.native_title)
                .textId(R.id.native_text)
                .mainImageId(R.id.native_main_image)
                .iconImageId(R.id.native_icon_image)
                .callToActionId(R.id.native_cta)
                .build();
        MoPubNativeAdRenderer adRenderer = new MoPubNativeAdRenderer(viewBinder);
        adAdapter = new MoPubAdAdapter(getActivity(), sampleAdapter);
        adAdapter.registerAdRenderer(adRenderer);
        sampleListView.setAdapter(adAdapter);

        DigitsAuthButton digitsButton = (DigitsAuthButton)rootView.findViewById(R.id.auth_button);
        digitsButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                // Do something with the session and phone number
            }

            @Override
            public void failure(DigitsException exception) {
                // Do something on failure
            }
        });

        loginButton = (TwitterLoginButton)rootView.findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });
        return rootView;
    }
}

