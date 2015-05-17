package me.tatocaster.ibsuoid.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import me.tatocaster.ibsuoid.R;
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

    public void onDestroy(){
        super.onDestroy();
        mRunning = false;
        Handler h = new Handler();
        h.removeCallbacksAndMessages(null);
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
                    JSONObject sisResponseObject = new JSONObject(response.getString("sis_response"));

                    // if fucking error occured. ugly json :(
                    if (sisResponseObject.has("error")) {
                        JSONObject error = new JSONObject(sisResponseObject.getString("error"));
                        Utilities.showNotification(TranscriptFetchService.this, "Error", error.getString("new_marks"));
                    } else if(sisResponseObject.has("new_marks") && Boolean.valueOf(sisResponseObject.getString("new_marks")) ){
                        Utilities.showNotification(TranscriptFetchService.this,
                                getResources().getString(R.string.service_notification_label),
                                getResources().getString(R.string.service_notification_label));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, null, prefs.getString("username", ""), prefs.getString("password", ""));
        return START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
