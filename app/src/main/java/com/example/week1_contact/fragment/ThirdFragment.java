package com.example.week1_contact.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.week1_contact.R;
import com.example.week1_contact.ThirdFragAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class ThirdFragment extends Fragment {

    private ArrayList<Player> players = new ArrayList<Player>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catchmind, container, false);

        ListView listview = (ListView)view.findViewById(R.id.playerList);

        //DB로 부터 player 정보 받아오는 부분/////
        Player playerA = new Player();
        playerA.setUserName("playerA");
        playerA.setUserScore("0");
        Player playerB = new Player();
        playerB.setUserName("playerB");
        playerB.setUserScore("1");

        players.add(playerA);
        players.add(playerB);
        //////////////////////////////////////////

        ThirdFragAdapter thirdFragAdapter = new ThirdFragAdapter(players);
        listview.setAdapter(thirdFragAdapter);

        return view;
    }

}
