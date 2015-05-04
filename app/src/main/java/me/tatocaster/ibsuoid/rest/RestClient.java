package me.tatocaster.ibsuoid.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.tatocaster.ibsuoid.Constants;
import me.tatocaster.ibsuoid.rest.service.ApiService;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by tatocaster on 5/4/2015.
 */
public class RestClient {
    // this must change , because i havenot my ftp right now
    private ApiService apiService;

    public RestClient() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'")
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(Constants.BASE_URL)
                .setConverter(new GsonConverter(gson))
//                .setRequestInterceptor(new SessionRequestInterceptor())
                .build();

        apiService = restAdapter.create(ApiService.class);
    }

    public ApiService getApiService() {
        return apiService;
    }
}
