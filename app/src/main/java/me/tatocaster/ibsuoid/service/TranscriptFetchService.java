package me.tatocaster.ibsuoid.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Response;

import org.json.JSONObject;

import me.tatocaster.ibsuoid.R;
import me.tatocaster.ibsuoid.network.VolleyClient;
import me.tatocaster.ibsuoid.ui.MainActivity;

/**
 * Created by tatocaster on 5/5/2015.
 */
public class TranscriptFetchService extends Service {
    private static final String TAG = "TranscriptFetchService";
    private boolean mRunning;
    NotificationManager mNotificationManager;

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
                Log.d(TAG, response.toString());
            }
        }, null, "11200125", prefs.getString("password", ""));

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(R.string.service_notification_label + "")
                .setContentText(R.string.service_notification_label + "")
                .setGroup("1")
//                .setOngoing(true)
                .setSmallIcon(R.drawable.avatar)
                .setContentIntent(resultPendingIntent);
        mNotificationManager.notify(10, builder.build());

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
