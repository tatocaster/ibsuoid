package me.tatocaster.ibsuoid;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * Created by tatocaster on 2015-05-17.
 */
public class Utilities {

    private static boolean isWIFI;

    public Utilities() {
    }

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

    public static boolean isWIFI(boolean connected){
        if(connected){
            return Utilities.isWIFI;
        }
        return false;
    }

}
