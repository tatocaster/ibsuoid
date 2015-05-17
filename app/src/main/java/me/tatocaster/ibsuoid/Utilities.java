package me.tatocaster.ibsuoid;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import me.tatocaster.ibsuoid.ui.MainActivity;


/**
 * Created by tatocaster on 2015-05-17.
 */
public class Utilities {

    private static boolean isWIFI;

    public Utilities() {
    }

    /**
     *
     * @param context
     * @return
     */
    public static boolean checkNetworkAvailability(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        try {
            Utilities.isWIFI = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isConnected;
    }

    /**
     *
      * @param connected
     * @return
     */
    public static boolean isWIFI(boolean connected){
        if(connected){
            return Utilities.isWIFI;
        }
        return false;
    }

    /**
     *
     * @param context
     */
    public static void showNotification(Context context) {
        NotificationManager mNotificationManager;
        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.service_notification_label))
                .setContentText(context.getResources().getString(R.string.service_notification_label))
                .setGroup("1")
//                .setOngoing(true)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setSmallIcon(R.mipmap.ic_stat_social_plus_one)
                .setContentIntent(resultPendingIntent);
        mNotificationManager.notify(10, builder.build());
    }


}
