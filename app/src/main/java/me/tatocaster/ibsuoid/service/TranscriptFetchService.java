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

import org.json.JSONException;
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

                try {
                    Log.d(TAG, response.toString());
                    showNotification(); // just to show that service is running, it must be within the if statement
                    String sisResponse = response.getString("sis_response");
                    JSONObject sisResponseObject = new JSONObject(sisResponse);
                    if (!Boolean.valueOf(sisResponseObject.getString("new_marks"))) {
                        return;
                    }
//                    showNotification();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, null, "11200125", prefs.getString("password", ""));
        return START_NOT_STICKY;
    }


    private void showNotification() {
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(this.getResources().getString(R.string.service_notification_label))
                .setContentText(this.getResources().getString(R.string.service_notification_label))
                .setGroup("1")
//                .setOngoing(true)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_stat_social_plus_one)
                .setContentIntent(resultPendingIntent);
        mNotificationManager.notify(10, builder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
