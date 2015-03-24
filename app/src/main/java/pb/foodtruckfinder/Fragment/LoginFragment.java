package pb.foodtruckfinder.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
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
import pb.foodtruckfinder.Twitter.SessionRecorder;

/**
 * Created by hugo on 20/03/15.
 */

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

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
                /**
                 * Get registration dialog if non match in db
                 */
                showCustomView();
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
                /**
                 * Get registration dialog if non match in db
                 */
                showCustomView();
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


    private EditText nameInput, emailInput;
    private View positiveAction;

    private void enableAction() {
        final String one = nameInput.getText().toString();
        final String two = emailInput.getText().toString();
        positiveAction.setEnabled(one.trim().length() > 0 && two.trim().length() > 0);
    }

    private void showCustomView() {
        final Activity activity = getActivity();
        MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title("Registrera")
                .customView(R.layout.dialog_register, true)
                .positiveText("Fortsätt")
                .negativeText("Avbryt")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        Log.d(TAG, nameInput.getText().toString() + " "  + emailInput.getText().toString());
                        ((MainActivity)activity).onAuthSuccess(true);
                    }
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        TwitterCore.getInstance().getSessionManager().clearActiveSession();
                        ((MainActivity)activity).onAuthSuccess(false);
                    }
                }).build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        nameInput = (EditText) dialog.getCustomView().findViewById(R.id.name);
        emailInput = (EditText) dialog.getCustomView().findViewById(R.id.email);

        nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableAction();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableAction();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        dialog.show();
        positiveAction.setEnabled(false); // disabled by default
    }
}

