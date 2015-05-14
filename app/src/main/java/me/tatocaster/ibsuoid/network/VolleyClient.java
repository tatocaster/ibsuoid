package me.tatocaster.ibsuoid.network;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.tatocaster.ibsuoid.Constants;

/**
 * Created by tatocaster on 5/14/2015.
 */

public class VolleyClient {

    private static VolleyClient mInstance;
    private Context context;
    private RequestQueue mRequestQueue;
    private final static String transcriptUrl = "/mobileapp/json-sis.php";

    public static VolleyClient getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyClient(context);
        }
        return mInstance;
    }

    private VolleyClient(Context context) {
        this.context = context;
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public RequestQueue getRequestQueue() {
        return this.mRequestQueue;
    }

    public void execute(Request request) {
        getRequestQueue().add(request);
        getRequestQueue().start();
    }

    public void getTranscript(final Response.Listener<JSONObject> response, final Response.ErrorListener error, String user, String password) {
        String transcriptFullUrl = Constants.BASE_URL + transcriptUrl;

        JSONObject jsonBody = null;
        try {
            jsonBody = new JSONObject("{\"sis_request\":{\"get\":{\"transcript\": {\"user\":\"" + user + "\",\"pwdh\":\"" + password + "\"}}}}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, transcriptFullUrl, jsonBody, response, error) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        execute(jsonObjectRequest);
    }

    public void checkNewMarks(final Response.Listener<JSONObject> response, final Response.ErrorListener error, String user, String password) {
        String transcriptFullUrl = Constants.BASE_URL + transcriptUrl;
        JSONObject jsonBody = null;
        try {
            jsonBody = new JSONObject("{\"sis_request\":{\"get\":{\"new_marks\": {\"user\":\"" + user + "\",\"pwdh\":\"" + password + "\"}}}}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, transcriptFullUrl, jsonBody, response, error) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        execute(jsonObjectRequest);
    }

}