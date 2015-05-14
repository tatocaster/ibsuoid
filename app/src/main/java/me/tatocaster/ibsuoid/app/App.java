package me.tatocaster.ibsuoid.app;

import android.app.Application;
import android.content.Context;

import me.tatocaster.ibsuoid.network.VolleyClient;

/**
 * Created by tatocaster on 5/4/2015.
 */
public class App extends Application
{

    private static Context context;
    private static VolleyClient v;

    @Override
    public void onCreate()
    {
        super.onCreate();
        App.context = getApplicationContext();
        App.v = VolleyClient.getInstance(context);
    }

    public static VolleyClient getVolleyClient()
    {
        return App.v;
    }
}
