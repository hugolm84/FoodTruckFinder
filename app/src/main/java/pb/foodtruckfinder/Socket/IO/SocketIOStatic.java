package pb.foodtruckfinder.Socket.IO;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Transport;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created by hugo on 27/03/15.
 */
public class SocketIOStatic {

    private static final String TAG = "SocketIOStatic";
    private static final String URL = SocketSession.SOCKET_URL;

    public static final String EVENT_SOCKET_MESSAGE         = "message";
    public static final String EVENT_SOCKET_LOCATION        = "location";
    public static final String EVENT_SOCKET_REMOTE_LOCATION = "remote_location";
    public static final String EVENT_SOCKET_CONNECTED       = "connected";
    public static final String EVENT_SOCKET_DISCONNECTED    = "disconnected";
    public static final String EVENT_SOCKET_DISCONNECT      = "disconnect";

    private SocketIOStatic() {}

    public static Socket socket(final String authToken) throws URISyntaxException {

        IO.Options options = new IO.Options();
        options.forceNew = true;
        options.reconnection = true;

        Socket socket = IO.socket(URL, options);

        socket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Transport transport = (Transport) args[0];
                transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        @SuppressWarnings("unchecked")
                        Map<String, String> headers = (Map<String, String>) args[0];
                        headers.put("authorization", "Bearer " + authToken);
                    }
                });
            }
        });
        return socket;
    }
}
