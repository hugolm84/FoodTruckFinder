package pb.foodtruckfinder.Socket.IO;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.twitter.sdk.android.core.AuthToken;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

import pb.foodtruckfinder.MainActivity;
import pb.foodtruckfinder.R;

/**
 * Created by hugo on 25/03/15.
 */
public class SocketSession {

    private SocketAuthCallback authCallback;
    private SocketRegisterCallback registerCallback;

    public interface SocketAuthCallback {
        void onAuthSuccess(final String token);
        void onAuthFail(final Exception e);
        void onNoSuchUser(final String identifier, final long digitsID);

    }
    public interface SocketRegisterCallback {
        void onRegisterSuccess(final String token);
        void onRegisterFail(final Exception e);

    }

    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private static final String PREF_NAME = "SOCKET_SESSION";
    private static final String PREF_USERID = "SOCKET_SESSION_ID";
    private static final String PREF_TOKEN = "SOCKET_SESSION_TOKEN";
    private static final String PREF_USERNAME = "SOCKET_SESSION_NAME";
    private static final String PREF_USERIMAGE = "SOCKET_SESSION_IMAGE";


    public static final String SOCKET_URL = "http://10.40.230.167:3000";//"http://10.0.1.43:3000"; //"http://10.40.230.72:3000";
    private static final String SOCKET_TOKEN_ENDPOINT = "/token";
    private static final String SOCKET_REGISTER_ENDPOINT = "/register";

    public SocketSession(Context ctx) {
        this(ctx, null, null);
    }

    public SocketSession(Context ctx, SocketAuthCallback cb1, SocketRegisterCallback cb2) {
        this.context = ctx;
        this.authCallback = cb1;
        this.registerCallback = cb2;
        this.preferences = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = preferences.edit();

    }

    public void setAuthCallback(SocketAuthCallback callback) {
        this.authCallback = callback;
    }

    public void setRegisterCallback(SocketRegisterCallback callback) {
        this.registerCallback = callback;
    }

    public void registerToken(final String token) {
        editor.putString(PREF_TOKEN, token);
        editor.commit();
    }

    private void clearToken() {
        editor.clear();
        editor.commit();
    }


    public void logout() {
        clearToken();
    }


    public final String getAuthToken() {
        final String token = preferences.getString(PREF_TOKEN, "");
        return token;
    }

    public final int getProfileImage() {
        final int resource = preferences.getInt(PREF_USERIMAGE, R.drawable.bunbun_prof);
        return resource;
    }

    public void authenticate(final String identifier, final Long digitsID) {
        SocketTokenTask task = new SocketTokenTask(identifier, digitsID);
        task.execute(SOCKET_URL + SOCKET_TOKEN_ENDPOINT);
        task.setMyTaskCompleteListener(new AbstractSocketTokenTask.OnTaskComplete() {
            @Override
            public void onAuthSuccess(String token) {
                registerToken(token);
                if(authCallback != null)
                    authCallback.onAuthSuccess(token);
            }

            @Override
            public void onAuthFailure(AbstractSocketTokenTask.SocketTaskException exception) {
                clearToken();
                if(authCallback != null) {
                    if(exception.getCode() == AbstractSocketTokenTask.SocketTaskException.NO_SUCH_USER)
                        authCallback.onNoSuchUser(identifier, digitsID);
                    else
                        authCallback.onAuthFail(exception);
                }
            }
        });
    }

    public void register(final String name, final int imageResource, final String identifier, final Long digitsID) {
        SocketRegisterTask task = new SocketRegisterTask(name, identifier, digitsID);
        task.execute(SOCKET_URL + SOCKET_REGISTER_ENDPOINT);
        task.setMyTaskCompleteListener(new AbstractSocketTokenTask.OnTaskComplete() {
            @Override
            public void onAuthSuccess(String token) {

                editor.putString(PREF_TOKEN, token);
                editor.putString(PREF_USERNAME, name);
                editor.putInt(PREF_USERIMAGE, imageResource);
                editor.putLong(PREF_USERID, digitsID);
                editor.commit();

                if(registerCallback != null)
                    registerCallback.onRegisterSuccess(token);
            }

            @Override
            public void onAuthFailure(AbstractSocketTokenTask.SocketTaskException exception) {
                clearToken();
                if(registerCallback != null)
                    registerCallback.onRegisterFail(exception);
            }
        });
    }
}
