package me.tatocaster.ibsuoid.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

import me.tatocaster.ibsuoid.Constants;

/**
 * Created by tatocaster on 5/5/2015.
 */
public class TranscriptFetchService extends IntentService{
    private static final String TAG = "TranscriptFetchService";

    public TranscriptFetchService() {
        super(TranscriptFetchService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Service Started!");

        // return fetched result to activity via this receiver
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");

        // get url from intent , this can be changed to get from Constants.java
        String url = intent.getStringExtra("url");

        Bundle bundle = new Bundle();

        if (!TextUtils.isEmpty(url)) {
            /* Update UI: Download Service is Running */
            receiver.send(Constants.SERVICE_STATUS_RUNNING, Bundle.EMPTY);

            try {
                String[] results = downloadData(url);
                // here will be parcelable object
                /* Sending result back to activity */
                if (null != results && results.length > 0) {
                    bundle.putStringArray("result", results);
                    receiver.send(Constants.SERVICE_STATUS_FINISHED, bundle);
                }
            } catch (Exception e) {
                /* Sending error message back to activity */
                bundle.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(Constants.SERVICE_STATUS_ERROR, bundle);
            }
        }
        Log.d(TAG, "Service Stopping!");
        this.stopSelf();
    }

    private String[] downloadData(String requestUrl) throws IOException, TranscriptFetchServiceException {
        // just dummy code , later will be fetching data
        String[] results = new String[0];
        return results;
    }



    public class TranscriptFetchServiceException extends Exception {

        public TranscriptFetchServiceException(String message) {
            super(message);
        }

        public TranscriptFetchServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
