package me.tatocaster.ibsuoid.rest.service;

import retrofit.http.GET;

/**
 * Created by tatocaster on 5/4/2015.
 */
public interface ApiService {

    @GET("/getUserSettings")
//    public void getUserSettings(Callback<User> callback);
    public void getUserSettings();


}
