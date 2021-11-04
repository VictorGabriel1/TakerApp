package com.example.taker;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Conexao {

    public static String postDados(String uri, File image){
        try{
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "image.jpg",
                            RequestBody.create(MediaType.parse("application/octet-stream"),
                                    image))
                    .build();

            Request request = new Request.Builder()
                    .url(uri)
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();

            Call call = client.newCall(request);
            Response response = call.execute();

            Log.d("request_log", String.valueOf(response.code()));
            return response.body().string();
        }catch (Exception e){
            Log.d("request_error", e.getMessage());
            e.printStackTrace();
            return null;
        }finally {
            Log.d("request_log", "foi");
        }

    }

}
