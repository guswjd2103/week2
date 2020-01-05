package com.example.week1_contact;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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

import java.io.File;
import java.security.MessageDigest;
import java.util.List;

import android.content.pm.Signature;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private Context mContext;
    protected String userName;
///////////
    Retrofit retrofit;
    RetrofitInterface retrofitInterface;
//////////

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();

        Intent intent = getIntent();
        userName = intent.getExtras().getString("username");

        getHashKey(mContext);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewPager = (ViewPager) findViewById(R.id.container);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("contact"));
        tabLayout.addTab(tabLayout.newTab().setText("photo"));
        tabLayout.addTab(tabLayout.newTab().setText("catchmind"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

//////////////////////
/*
        retrofit = new Retrofit.Builder().baseUrl(retrofitInterface.API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);

        Call<List<ContactData>> comment = retrofitInterface.sendUserName("test");
        comment.enqueue(new Callback<List<ContactData>>() {
            @Override
            public void onResponse(Call<List<ContactData>> call, Response<List<ContactData>> response) {
                List<ContactData> contacts = response.body();
                for(int i = 0 ; i< contacts.size();i ++) {
                    Log.d("asdf", "start: " + contacts.get(i).getName() + " :end");
                }
            }

            @Override
            public void onFailure(Call<List<ContactData>> call, Throwable t) {
                Log.v("error",t.getMessage());
            }
        });


        Call<List<ContactData>> comment2 = retrofitInterface.getContacts("test");
        comment2.enqueue(new Callback<List<ContactData>>() {
            @Override
            public void onResponse(Call<List<ContactData>> call, Response<List<ContactData>> response) {
                List<ContactData> contacts = response.body();
                Log.d("qwer", "왔");
                for(int i = 0 ; i < contacts.size(); i++){
                    Log.d("qewr", "contacts : "+contacts.get(i).getName());
                }
            }

            @Override
            public void onFailure(Call<List<ContactData>> call, Throwable t) {
                Log.d("qwer", "안왔");
            }
        });
*/



/////////////////////
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), userName);
        Log.d("frag_con","보낸 유저네임 -main:" + userName);
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
    }

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
