package com.example.kettle.networking;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.kettle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * HTTP GET method to call a nodejs server
 */

public class HttpGetRequest extends AsyncTask<Object, Void, JSONArray> {
    View view;
    static final String REQUEST_METHOD = "GET";
    static final int READ_TIMEOUT = 15000;
    static final int CONNECTION_TIMEOUT = 15000;
    public JSONArray arrayResult;

    /**
     * Calls GET to return JSONArray of leaderboard info
     * @param objects
     * @return
     */
    @Override
    protected JSONArray doInBackground(Object... objects){
        String result;
        String inputLine;
        view = (View) objects[0];

        try {
            // connect to the server
            URL myUrl = new URL("https://kettlex-server.herokuapp.com/getLeaderBoard");
            HttpURLConnection connection =(HttpURLConnection) myUrl.openConnection();
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.connect();

            // get the string from the input stream
            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            while((inputLine = reader.readLine()) != null){
                stringBuilder.append(inputLine);
            }
            reader.close();
            streamReader.close();
            result = stringBuilder.toString();
            System.out.println("GET result: " + result);
            JSONArray jsonArray = new JSONArray(result);
            return jsonArray;

        } catch(IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets information in the leaderboard listview.
     * @param jsonArray
     */
    protected void onPostExecute(JSONArray jsonArray){


    }
}

