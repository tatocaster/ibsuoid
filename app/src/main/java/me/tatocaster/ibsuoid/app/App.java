package me.tatocaster.ibsuoid.app;

import android.app.Application;

import me.tatocaster.ibsuoid.network.VolleyClient;

/**
 * Created by tatocaster on 5/4/2015.
 */
public class App extends Application
{
    private static VolleyClient v;

    @Override
    public void onCreate()
    {
        super.onCreate();
        App.v = VolleyClient.getInstance(this);
    }

    public static VolleyClient getVolleyClient()
    {
        return App.v;
    }
}
