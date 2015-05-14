package me.tatocaster.ibsuoid.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by tatocaster on 5/5/2015.
 */
public class TranscriptFetchService extends Service {
    private static final String TAG = "TranscriptFetchService";
    private boolean mRunning;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (!mRunning) {
            mRunning = true;
            Log.v(TAG,"Service is running");
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
