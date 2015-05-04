package me.tatocaster.ibsuoid.app;

import android.app.Application;

import me.tatocaster.ibsuoid.rest.RestClient;

/**
 * Created by tatocaster on 5/4/2015.
 */
public class App extends Application
{
    private static RestClient restClient;

    @Override
    public void onCreate()
    {
        super.onCreate();

        restClient = new RestClient();
    }

    public static RestClient getRestClient()
    {
        return restClient;
    }
}
