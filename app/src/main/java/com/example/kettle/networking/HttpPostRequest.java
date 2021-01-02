package com.example.kettle.networking;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * POST method for nodejs server to post to Kettlex in mongodb instance.
 */
public class HttpPostRequest extends AsyncTask<Object, Void, Void> {
    static final String REQUEST_METHOD = "POST";
    static final int READ_TIMEOUT = 15000;
    static final int CONNECTION_TIMEOUT = 15000;

    /**
     * POSTS to our nodejs server.
     * @param objects
     * @return
     */
    @Override
    protected Void doInBackground(Object... objects) {
        HttpURLConnection urlConn = null;
        String result = "";
        JSONObject json = new JSONObject();
        try {
            json.put("User", (String) objects[0]);
            json.put("Title", (String) objects[1]);
            json.put("Body", (String) objects[2]);
            URL url;
            DataOutputStream printout;
            String address = "https://kettlex-server.herokuapp.com/kettlex/post";
            Log.d("sendPost", address);
            url = new URL (address);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoInput (true);
            urlConn.setDoOutput (true);
            urlConn.setUseCaches (false);
            urlConn.setRequestMethod("POST");
            urlConn.setChunkedStreamingMode(100);
            urlConn.setRequestProperty("Content-Type","application/json");
            urlConn.connect();
            // Send POST output.
            DataOutputStream os = new DataOutputStream(urlConn.getOutputStream());
            os.writeBytes(json.toString());
            os.flush();
            os.close();

            Log.i("STATUS", String.valueOf(urlConn.getResponseCode()));
            Log.i("MSG" , urlConn.getResponseMessage());

            urlConn.disconnect();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }  finally {
            if(urlConn !=null)
                urlConn.disconnect();
        }
        return null;
    }
}
