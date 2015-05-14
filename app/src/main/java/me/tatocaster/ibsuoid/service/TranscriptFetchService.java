package me.tatocaster.ibsuoid.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Response;

import org.json.JSONObject;

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
        VolleyClient.getInstance(this).checkNewMarks(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
            }
        }, null, "11200125", "rocker");
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
