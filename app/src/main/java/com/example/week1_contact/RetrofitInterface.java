package com.example.week1_contact;

import org.json.JSONArray;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RetrofitInterface {
    public static final String API_URL = "http://192.249.19.251:0280/";

    @GET("getcontact")
    Call<List<ContactData>> sendUserName(@Query("username") String username);

    //user이름 주면 contacts 받아옴
    @FormUrlEncoded
    @POST("contact")
    Call<List<ContactData>> getContacts(@Field("username") String username);

    //
    @FormUrlEncoded
    @POST("contact")
    Call<List<ContactData>> sendContacts(@Field("username")String username, @Field("contacts")JSONArray jsonArray);
}