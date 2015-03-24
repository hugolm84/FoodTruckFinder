package pb.foodtruckfinder.Fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import android.widget.Toast;


import pb.foodtruckfinder.MainActivity;
import pb.foodtruckfinder.R;
import pb.foodtruckfinder.Twitter.SessionRecorder;

/**
 * Created by hugo on 20/03/15.
 */

public class LoginFragment extends Fragment {

    protected static final String ARG_SECTION_NUMBER = "section_number";

    private TwitterLoginButton twitterButton;
    private DigitsAuthButton phoneButton;


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static LoginFragment newInstance(int sectionNumber) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    public LoginFragment() {
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twitterButton.onActivityResult(requestCode, resultCode,
                data);
        twitterButton.setVisibility(View.GONE);
        phoneButton.setVisibility(View.GONE);
    }

    private void setUpTwitterButton(final View view) {
        twitterButton = (TwitterLoginButton)view.findViewById(R.id.twitter_login_button);
        twitterButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                SessionRecorder.recordSessionActive("Login: twitter account active", result.data);
                ((MainActivity)getActivity()).onAuthSuccess();
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.toast_twitter_signin_fail),
                        Toast.LENGTH_SHORT).show();
                Crashlytics.logException(exception);
            }
        });
    }

    private void setUpDigitsButton(final View view) {
        phoneButton = (DigitsAuthButton)view.findViewById(R.id.digits_button);
        phoneButton.setAuthTheme(R.style.AppTheme);
        phoneButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession digitsSession, String phoneNumber) {
                SessionRecorder.recordSessionActive("Login: digits account active", digitsSession);
                ((MainActivity)getActivity()).onAuthSuccess();
            }

            @Override
            public void failure(DigitsException e) {
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.toast_twitter_digits_fail),
                        Toast.LENGTH_SHORT).show();
                Crashlytics.logException(e);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            phoneButton
                .setAuthTheme(android.R.style.Theme_Material);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setUpDigitsButton(rootView);
        setUpTwitterButton(rootView);
        return rootView;
    }
}

