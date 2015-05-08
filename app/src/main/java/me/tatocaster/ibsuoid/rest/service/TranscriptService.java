package me.tatocaster.ibsuoid.rest.service;

import retrofit.http.POST;

/**
 * Created by tatocaster on 5/8/2015.
 */
interface TranscriptService {
    @POST("")
    public void getNewMarks();
}
