package com.example.adutucartrider.services;

import android.util.Log;

import com.google.common.net.MediaType;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Request;


public class sendNotification {

    private final String URL = "https://fcm.googleapis.com/fcm/send";

    private final String key = "key=AAAAq8qjKhI:APA91bGVJs0LZXmEqDTXgGk6XXwIaNpIz2WJWE10xVxCdKnuQ1IdDqnIMFzUYE48iTwGS1NWCmgx9N722R70JCZSiZ33LOoAv67rb_uMxjhQl0qjkoaMZ08smpXcAcghCk4iEfnCTd60";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public sendNotification(){}

    public void sendNotificationToCustomer(String pickupKey,String token) throws IOException {

        httpRequestClient(token,pickupKey);
    }

    public void sendNotificationToRider(String pickupKey,String token) throws IOException {
        httpRequestClient(token,pickupKey);

    }

    public void sendNotificationToChannel(String orderKey,String riderName,String channelName) throws IOException {
       // httpRequestClientAdmin(orderKey,channelName);

    }


    public void httpRequestClient(String token,String pickupKey) throws IOException {


        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();



        try {
            jsonObject.put("body","BODY");
            jsonObject.put("title","TITLE");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        RequestBody requestBody = new FormBody.Builder()
                .add("to",token)
                .add("notification", String.valueOf(jsonObject))
                .build();


        Request request = new Request.Builder()
                .header("Authorization",key )
                .url(URL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("TAG",response.body().string());

            }
        });
    }

    public void httpRequestClientAdmin(String orderKey,String channelName) throws IOException {


        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();

        JSONObject notifcationBody = new JSONObject();
        JSONObject notification = new JSONObject();
        try {
            notifcationBody.put("title", orderKey);
            notifcationBody.put("message", "not calculated");
            notification.put("to", "topic/"+channelName);
            notification.put("data", notifcationBody);
            Log.e("TAG", "try");
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }


//        try {
//            jsonObject.put("body","BODY");
//            jsonObject.put("title","TITLE");
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }


        RequestBody requestBody = new FormBody.Builder()
                .add("to","topic/"+channelName)
                .add("notification", String.valueOf(notifcationBody))
                .build();


        Request request = new Request.Builder()
                .header("Authorization",key )
                .url(URL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("TAG",response.body().string());

            }
        });
    }

}
