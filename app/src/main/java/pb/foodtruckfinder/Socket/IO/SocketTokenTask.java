package pb.foodtruckfinder.Socket.IO;

import org.apache.http.client.methods.HttpPost;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by hugo on 25/02/15.
 */

public class SocketTokenTask extends AbstractSocketTokenTask {

    private static final String TAG = "SocketTokenTask";

    private final String mPhoneNr;
    private final Long mDigitsId;


    public SocketTokenTask(final String phoneNr, final Long digitsId) {
        super();
        mPhoneNr = phoneNr;
        mDigitsId = digitsId;
    }

    @Override
    protected Boolean doInBackground(String... urls) {
        try {
            final URL url = new URL(urls[0]);
            HttpPost httppost = new HttpPost(urls[0]);
            httppost.setHeader("digits_id", String.valueOf(mDigitsId));
            httppost.setHeader("digits", mPhoneNr);
            return doHttpPostWork(httppost);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

