package com.example.week1_contact;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.week1_contact.fragment.ContactFragment;
import com.example.week1_contact.fragment.PhotoFragment;
import com.example.week1_contact.fragment.ThirdFragment;

public class PagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;
    String username;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, String username) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.username = username;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        switch (position) {
            case 0:
                ContactFragment tab1 = new ContactFragment();
                bundle.putString("username",username);
                tab1.setArguments(bundle);
                Log.d("frag_con","보낸 유저네임 -어뎁터 :"+username);
                return tab1;
            case 1:
                PhotoFragment tab2 = new PhotoFragment();
                bundle.putString("username",username);
                tab2.setArguments(bundle);
                return tab2;
            case 2:
                ThirdFragment tab3 = new ThirdFragment();
                bundle.putString("username",username);
                tab3.setArguments(bundle);
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}