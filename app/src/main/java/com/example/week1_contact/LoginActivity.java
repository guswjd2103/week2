package com.example.week1_contact;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.internal.Utility;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginActivity extends AppCompatActivity {
    private Context mContext;

    private LoginButton btn_facebook_login;
    private LoginCallback mLoginCallback;
    private CallbackManager mCallbackManager;

    Retrofit retrofit;
    RetrofitInterface retrofitInterface;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = getApplicationContext();
        mCallbackManager = CallbackManager.Factory.create(); //로그인 응답을 처리할 콜백 관리자 생성
//        mLoginCallback = new LoginCallback();

        btn_facebook_login = (com.facebook.login.widget.LoginButton) findViewById(R.id.btn_facebook_login);
        btn_facebook_login.setReadPermissions(Arrays.asList("public_profile", "email"));
//        btn_facebook_login.registerCallback(mCallbackManager, mLoginCallback);

        //facebook login
        btn_facebook_login.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("Callback :: ", "onSuccess");
                requestMe(loginResult.getAccessToken());
                AccessToken accessTocken = loginResult.getAccessToken();

                GraphRequest request = GraphRequest.newMeRequest(accessTocken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("TAG", "페이스북 로그인 결과" + response.toString());
                        try{
                            String email = object.getString("email");
                            String name = object.getString("name");
                            userName = name;
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("username",userName);
                            Log.d("frag_con","보낸 유저네임 -login:"+userName);
                            startActivity(intent);

                            Log.d("TAG","페이스북 이메일 ->"+ email);
                            Log.d("TAG", "페이스북 이름 ->" + name);
                        }
                        catch(Exception e){
                            e.printStackTrace();
                            Log.d("실패", "페이스북 정보가져오기 실패");
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 사용자 정보 요청
    public void requestMe(AccessToken token) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.e("result",object.toString());

                        //서버에 username, password 보내기
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }
}
