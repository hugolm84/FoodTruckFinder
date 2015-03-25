package pb.foodtruckfinder.Socket.IO;

import org.apache.http.client.methods.HttpPost;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by hugo on 24/03/15.
 */
public class SocketRegisterTask extends AbstractSocketTokenTask {

    private final String name, phoneNumber;
    private final long digitsId;

    public SocketRegisterTask(final String name, final String phoneNumber, final long digitsId) {
        super();
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.digitsId = digitsId;
    }

    @Override
    protected Boolean doInBackground(String... urls) {
        try {
            final URL url = new URL(urls[0]);
            HttpPost httppost = new HttpPost(urls[0]);
            httppost.setHeader("name", name);
            httppost.setHeader("digits_id", String.valueOf(digitsId));
            httppost.setHeader("digits", phoneNumber);
            return doHttpPostWork(httppost);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
