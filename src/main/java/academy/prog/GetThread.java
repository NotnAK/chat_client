package academy.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class GetThread implements Runnable {
    private final Gson gson;
    private int n; // /get?from=n
    private String username;
    private final Set<Message> receivedMessages;

    public GetThread(String username) {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        this.username = username;
        this.receivedMessages = new HashSet<>();

    }

    @Override
    public void run() { // WebSockets
        try {
            while ( ! Thread.interrupted()) {
                URL url = new URL(Utils.getURL() + "/get?from=" + n + "&username=" + username);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();

                InputStream is = http.getInputStream();
                try {
                    byte[] buf = responseBodyToArray(is);
                    String strBuf = new String(buf, StandardCharsets.UTF_8);

                    JsonMessages list = gson.fromJson(strBuf, JsonMessages.class);
                    if (list != null) {
                        for (Message m : list.getList()) {
                            if(receivedMessages.add(m)){
                                System.out.println(m);
                            }
                            n++;
                        }
                    }

                } finally {
                    is.close();
                }

                // C -> S -> x
                // WebSockets

                Thread.sleep(500);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private byte[] responseBodyToArray(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[10240];
        int r;

        do {
            r = is.read(buf);
            if (r > 0) bos.write(buf, 0, r);
        } while (r != -1);

        return bos.toByteArray();
    }
}
