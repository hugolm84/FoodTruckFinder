package pb.foodtruckfinder.Socket.IO;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

/**
 * Created by hugo on 25/02/15.
 */

abstract class AbstractSocketTokenTask extends AsyncTask<String, Void, Boolean> {

    private final static String TAG = "AbstractSocketTokenTask";

    public class SocketTaskException extends Exception {

        public final static int NO_SUCH_USER = 401;

        private int code;
        SocketTaskException() {};
        SocketTaskException(Exception e, int code) {
            super(e);
            this.code = code;
        }

        SocketTaskException(final String msg, final int code) {
            super(msg);
            this.code = code;
        }

        public final int getCode() {
            return code;
        }
    }

    protected SocketTaskException exception;
    protected String token;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    private OnTaskComplete onTaskComplete;

    public interface OnTaskComplete {
        public void onAuthSuccess(final String token);
        public void onAuthFailure(final SocketTaskException exception);
    }

    public void setMyTaskCompleteListener(OnTaskComplete onTaskComplete) {
        this.onTaskComplete = onTaskComplete;
    }

    public AbstractSocketTokenTask() {

    }

    protected boolean onHttpPostDone(final JSONObject response) {
        try {
            token = response.getString("token");
            if(token != null && !token.isEmpty())
                return true;
            exception = new SocketTaskException("Missing token key in response", 401);
        } catch (JSONException e) {
            exception = new SocketTaskException(e, 401);
        }
        return false;
    }

    @Override
    abstract protected Boolean doInBackground(String... urls);

    protected Boolean doHttpPostWork(HttpPost post) {
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(post);
            int status = response.getStatusLine().getStatusCode();

            if (status == 200) {
                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);
                JSONObject json = new JSONObject(data);
                return onHttpPostDone(json);
            } else {
                exception = new SocketTaskException(response.getStatusLine().getReasonPhrase(), status);
            }
        } catch (IOException | JSONException e) {
            exception = new SocketTaskException(e, 500);
        }
        return false;
    }

    protected void onPostExecute(Boolean success) {
        if(success)
            onTaskComplete.onAuthSuccess(token);
        else
            onTaskComplete.onAuthFailure(exception);
    }
}

