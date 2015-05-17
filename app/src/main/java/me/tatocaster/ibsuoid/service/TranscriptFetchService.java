package me.tatocaster.ibsuoid.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import me.tatocaster.ibsuoid.Utilities;
import me.tatocaster.ibsuoid.network.VolleyClient;

/**
 * Created by tatocaster on 5/5/2015.
 */
public class TranscriptFetchService extends Service {
    private static final String TAG = "TranscriptFetchService";
    private boolean mRunning;

    @Override
    public void onCreate() {
        super.onCreate();
        mRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mRunning) {
            mRunning = true;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        VolleyClient.getInstance(this).checkNewMarks(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    Log.d(TAG, response.toString());
                    String sisResponse = response.getString("sis_response");
                    JSONObject sisResponseObject = new JSONObject(sisResponse);
                    if (Boolean.valueOf(sisResponseObject.getString("new_marks"))) {
                        Utilities.showNotification(TranscriptFetchService.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, null, "11200125", prefs.getString("password", ""));
        return START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
