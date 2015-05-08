package me.tatocaster.ibsuoid.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by tatocaster on 5/8/2015.
 */
public class TranscriptBroadcastReceiver extends BroadcastReceiver {

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent myIntent = new Intent(context, TranscriptFetchService.class);
        context.startService(myIntent);
    }

}
