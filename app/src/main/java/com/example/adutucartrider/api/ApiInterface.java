package com.example.adutucartrider.api;

import static com.example.adutucartrider.models.Constants.API_KEY;
import static com.example.adutucartrider.models.Constants.CONTENT_TYPE;

import com.example.adutucartrider.services.pushNotification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {

    @Headers({"Authorization:"+API_KEY, "Content-Type:"+CONTENT_TYPE})
    @POST("fcm/send")
    Call<pushNotification> sendNotification(@Body pushNotification notification);
}
