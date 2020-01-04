package com.example.week1_contact;

import com.google.gson.JsonArray;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetrofitInterface {
    @GET("contact")
    Call<User> getFirst(@Path("userID") String id);
}
