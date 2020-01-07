package com.example.week1_contact.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.week1_contact.R;
import com.example.week1_contact.ThirdFragAdapter;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class ThirdFragment extends Fragment {

    private String TAG = "ThirdFragment";
    private Socket mSocket;
    private ArrayList<Room> rooms = new ArrayList<Room>();
    private String userName;
    private ArrayList<String> userList = new ArrayList<String>();
    int userNum;
    Room roomA;
    ThirdFragAdapter thirdFragAdapter;
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        try {
//            mSocket = IO.socket("http://192.249.19.251:0280");
////            mSocket.connect();
////            mSocket.on(Socket.EVENT_CONNECT, onConnect); //Socket.EVENT_CONNECT : 연결이 성공하면 발생하는 이벤트, onConnect : callback 객체
////            mSocket.on("serverMessage", onMessageReceived); // serverMessage 이벤트로 오는 메시지를 받기 위한 call back 객체 : onMessageReceived
////            mSocket.on("countUsers", onCountUsers);
//            mSocket.on(Socket.EVENT_CONNECT, (Object... objects) -> {
//                mSocket.emit("clientMessage", "hi"); //서버쪽으로 이벤트 발생시키기
//                mSocket.emit("countUsers", "count users");
//            }).on("serverMessage", (Object... objects) -> {
//                try {
//                    JSONObject receivedData = (JSONObject)objects[0];
//                    Log.d(TAG, receivedData.getString("msg"));
//                    Log.d(TAG, receivedData.getString("data"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }).on("countUserNum", (Object... objects) -> {
//                Log.d("countUserNum", "socket response");
//                try {
//                    JSONObject receivedCount = (JSONObject) objects[0];
//                    userNum = receivedCount.getInt("userNum");
//                    Log.d("usernum", Integer.toString(receivedCount.getInt("userNum")));
//                    Log.d("user list", receivedCount.getJSONArray("users").toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            });
//        } catch(URISyntaxException e) {
//            e.printStackTrace();
//        }
//    }

    private Handler mHandler = new Handler(Looper.getMainLooper());
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rooms.clear();
        SharedPreferences sf= getContext().getSharedPreferences("USERSIGN", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();

        View view = inflater.inflate(R.layout.fragment_catchmind, container, false);
        ListView listview = (ListView)view.findViewById(R.id.playerList);

        Bundle bundle = this.getArguments();
        if(bundle != null){
            bundle = getArguments();
            userName = bundle.getString("username");
        }


        //DB로 부터 player 정보 받아오는 부분/////
        roomA = new Room();
        roomA.setUserNum(userNum);
        roomA.setUserScore("0");
        roomA.setRoomNum("날 이겨봐");
        Room roomB = new Room();
        roomB.setRoomNum("즐겜하실분");
        roomB.setUserScore("0");

        rooms.add(roomA);
        rooms.add(roomB);
        //////////////////////////////////////////

        thirdFragAdapter = new ThirdFragAdapter(rooms, userName, userNum);
        listview.setAdapter(thirdFragAdapter);

        try {
            mSocket = IO.socket("http://192.249.19.251:0280");
            mSocket.connect();
            mSocket.on(Socket.EVENT_CONNECT, onConnect); //Socket.EVENT_CONNECT : 연결이 성공하면 발생하는 이벤트, onConnect : callback 객체
            mSocket.on("serverMessage", onMessageReceived); // serverMessage 이벤트로 오는 메시지를 받기 위한 call back 객체 : onMessageReceived
            mSocket.on("countUserNum", onCountUsers);
        } catch(URISyntaxException e) {
            e.printStackTrace();
        }

        return view;
    }

    // Socket서버에 connect 됨과 동시에 발생하는 객체
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mSocket.emit("clientMessage", "hi"); //서버쪽으로 이벤트 발생시키기
            mSocket.emit("countUsers", "count users");
            //방 번호, username을 보냄
        }
    };

    // 서버에서 serverMessage이벤트를 발생시켜 보내는 메시지를 받는 객체
    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // 전달받은 데이터 : JSON으로 서버에서 보냄
            try {
                JSONObject receivedData = (JSONObject) args[0];
                Log.d(TAG, receivedData.getString("msg"));
                Log.d(TAG, receivedData.getString("data"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
//
    private Emitter.Listener onCountUsers = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // 전달받은 데이터 : JSON으로 서버에서 보냄
            Log.d("count users", "socket");
            try {
                JSONObject receivedCount = (JSONObject) args[0];
                int userNumUpdate = receivedCount.getInt("userNum");
                Log.d("usernum", Integer.toString(userNumUpdate));
                Log.d("user list", receivedCount.getJSONArray("users").toString());
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                roomA.setUserNum(userNumUpdate);
                                userNum = userNumUpdate;
                                thirdFragAdapter.notifyDataSetChanged();
                                getView().invalidate();
                            }
                        });
                    }
                };
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


}
