package pb.foodtruckfinder.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import android.widget.EditText;
import android.widget.Toast;


import pb.foodtruckfinder.MainActivity;
import pb.foodtruckfinder.R;
import pb.foodtruckfinder.Socket.IO.SocketSession;

/**
 * Created by hugo on 20/03/15.
 */

public class LoginFragment extends Fragment implements SocketSession.SocketAuthCallback, SocketSession.SocketRegisterCallback {

    private static final String TAG = "LoginFragment";

    protected static final String ARG_SECTION_NUMBER = "section_number";

    private SocketSession mSession;
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
               mSession.authenticate(result.data.getUserName(), result.data.getUserId());
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
            public void success(DigitsSession session, String phoneNumber) {
                mSession.authenticate(phoneNumber, session.getId());
            }

            @Override
            public void failure(DigitsException exception) {
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.toast_twitter_digits_fail),
                        Toast.LENGTH_SHORT).show();
                Crashlytics.logException(exception);
                mSession.authenticate("+460704915129", 3099380872L);
            }
        });

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
        mSession = new SocketSession(getActivity(), this, this);
        setUpDigitsButton(rootView);
        setUpTwitterButton(rootView);
        return rootView;
    }

    @Override
    public void onAuthSuccess(String token) {
        Log.d(TAG, "Authenticated! " + token);
        ((MainActivity)getActivity()).onAuthSuccess();

    }

    @Override
    public void onAuthFail(Exception e) {
        Log.d(TAG, "Failed to auth! " + e.getMessage());
        ((MainActivity)getActivity()).onAuthFailure();
    }

    @Override
    public void onNoSuchUser(String identifier, long digitsID) {
        Log.d(TAG, "No such user!");
        showCustomView(identifier, digitsID);

    }

    @Override
    public void onRegisterSuccess(String token) {
        ((MainActivity)getActivity()).onAuthSuccess();
    }

    @Override
    public void onRegisterFail(Exception e) {
        ((MainActivity)getActivity()).onAuthFailure();
    }

    private EditText nameInput;
    private View positiveAction;

    private void showCustomView(final String identifier, final long id) {
        final Activity activity = getActivity();
        MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title(R.string.dialog_register)
                .customView(R.layout.dialog_register, true)
                .positiveText(R.string.dialog_positive)
                .negativeText(R.string.dialog_negative)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        mSession.register(nameInput.getText().toString(), identifier, id);
                    }
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        TwitterCore.getInstance().getSessionManager().clearActiveSession();
                        ((MainActivity)activity).onAuthFailure();
                    }
                }).build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        nameInput = (EditText) dialog.getCustomView().findViewById(R.id.name);

        nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        dialog.show();
        positiveAction.setEnabled(false); // disabled by default
    }
}

