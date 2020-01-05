package com.example.week1_contact;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;


import java.net.URISyntaxException;
import java.security.MessageDigest;
import android.content.pm.Signature;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

//    private String TAG = "MainActivity";
//    private Socket mSocket;
    private ViewPager mViewPager;
    private Context mContext;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();

        getHashKey(mContext);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewPager = (ViewPager) findViewById(R.id.container);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("contact"));
        tabLayout.addTab(tabLayout.newTab().setText("photo"));
        tabLayout.addTab(tabLayout.newTab().setText("catchmind"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

//        try {
//            mSocket = IO.socket("http://192.249.19.251:0280");
//            mSocket.connect();
//            mSocket.on(Socket.EVENT_CONNECT, onConnect); //Socket.EVENT_CONNECT : 연결이 성공하면 발생하는 이벤트, onConnect : callback객체
//            mSocket.on("serverMessage", onMessageReceived); // serverMessage 이벤트로 오는 메시지를 받기 위한 call back 객체 : onMessageReceived
//            mSocket.on("newUser", onNewUser); //서버에서 보내는 newUser이벤트로 오는 것을 받기 위한 객체
//        } catch(URISyntaxException e) {
//            e.printStackTrace();
//        }
    }

//    // Socket서버에 connect 됨과 동시에 발생하는 객체
//    private Emitter.Listener onConnect = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            mSocket.emit("clientMessage", "hi"); //서버쪽으로 이벤트 발생시키기
//            //방 번호, username을 보냄
//        }
//    };
//
//    // 서버에서 serverMessage이벤트를 발생시켜 보내는 메시지를 받는 객체
//    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            // 전달받은 데이터 : JSON으로 서버에서 보냄
//            try {
//                JSONObject receivedData = (JSONObject) args[0];
//                Log.d(TAG, receivedData.getString("msg"));
//                Log.d(TAG, receivedData.getString("data"));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    };
//
//    //서버 방 입장
//    private Emitter.Listener onNewUser = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//
//        }
//    };

    @Nullable
    public static String getHashKey(Context context) {
        final String TAG = "KeyHash";
        String keyHash = null;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                keyHash = new String(Base64.encode(md.digest(), 0));
                Log.d(TAG, keyHash);
            }

        } catch (Exception e) {
            Log.e("name not found", e.toString());
        }

        if(keyHash != null) {
            return keyHash;
        } else {
            return null;
        }
    }

    private View createTabView(String tabName) {
        View tabView = LayoutInflater.from(mContext).inflate(R.layout.custom_tab, null);
        TextView txt_name = (TextView) tabView.findViewById(R.id.txt_name);
        txt_name.setText(tabName);
        return tabView;

    }
}
