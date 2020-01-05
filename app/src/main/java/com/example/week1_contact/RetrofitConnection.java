package com.example.week1_contact;

import java.net.URL;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConnection {

    String URL = "/";
    Retrofit retrofit = new Retrofit.Builder().baseUrl(URL).addConverterFactory(GsonConverterFactory.create()).build();
    RetrofitInterface server = retrofit.create(RetrofitInterface.class);

    /*
    private static final String URL = "/";
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(URL).addConverterFactory(GsonConverterFactory.create()).client(httpClient.build());
    private static Retrofit retrofit = builder.build();

    public static <S> S createService(Class<S> serviceClass){
        return retrofit.create(serviceClass);
    }

   */
}
