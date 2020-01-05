package com.example.week1_contact;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;


import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class gameActivity extends Activity {

    private String TAG = "GameActivity";
    private Socket mSocket;
    private String answer;
    private boolean Offense_Defense = false;        //true : 그리기 & false : 맞추기
    String userName;
    int roomNumber = -1;

    ArrayList<Point> points = new ArrayList<Point>();
    int color = Color.BLACK;
    LinearLayout drawlinear;

    class Point{
        float x;
        float y;
        boolean check;
        int color;

        public Point(float x, float y, boolean check, int color){
            this.x = x;
            this.y = y;
            this.check = check;
            this.color = color;
        }
    }

    class MyView extends View{
        public MyView(Context context){super(context);}

        @Override
        protected void onDraw(Canvas canvas) {
            Paint p = new Paint();
            p.setStrokeWidth(15);
            for(int i=1; i<points.size(); i++){
                p.setColor(points.get(i).color);
                if(!points.get(i).check)
                    continue;
                canvas.drawLine(points.get(i-1).x,points.get(i-1).y, points.get(i).x,points.get(i).y,p);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN :
                    points.add(new Point(x,y,false, color));
                case MotionEvent.ACTION_MOVE :
                    points.add(new Point(x,y,true,color));
                    break;
                case MotionEvent.ACTION_UP :
                    break;
            }
            invalidate();
            return true;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameboard);

        try {
            mSocket = IO.socket("http://192.249.19.251:0280");
            mSocket.connect();
            mSocket.on(Socket.EVENT_CONNECT, onConnect); //Socket.EVENT_CONNECT : 연결이 성공하면 발생하는 이벤트, onConnect : callback객체
            mSocket.on("newUser", onNewUser); //서버에서 보내는 newUser이벤트로 오는 것을 받기 위한 객체
        } catch(URISyntaxException e) {
            e.printStackTrace();
        }

        //서버로 부터 random 하게 제시어 받아오기
        answer = "apple";

        Intent intent = getIntent();
        userName = intent.getExtras().getString("name");
        roomNumber = intent.getExtras().getInt("roomNumber");

        TextView textView = (TextView)findViewById(R.id.playerName);
        textView.setText(userName);

        final MyView m = new MyView(this);
        drawlinear = findViewById(R.id.drawCanvas);
        drawlinear.addView(m);

        Button redButton = (Button)findViewById(R.id.redButton);
        Button blueButton = (Button)findViewById(R.id.blueButton);
        Button yellowButton = (Button)findViewById(R.id.yellowButton);

        redButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                color = Color.RED;
            }
        });
        blueButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                color = Color.BLUE;
            }
        });
        yellowButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                color = Color.YELLOW;
            }
        });

        Button resetButton = (Button)findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                points.clear();
                m.invalidate();
            }
        });

        Button answerButton = (Button)findViewById(R.id.answerButton);
        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonObject answerObject = new JsonObject();
//                answerObject.addProperty("answer", )
                EditText answer_u = (EditText)findViewById(R.id.answer);
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(answer_u.getWindowToken(),0);
                //정답과 비교해서 턴을 넘겨주는?
                if(Offense_Defense == true) return;
                if(answer.equals(answer_u.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "InCorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    // Socket서버에 connect 됨과 동시에 발생하는 객체
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject entranceinfo = new JSONObject();
            try {
                entranceinfo.put("username", userName);
                entranceinfo.put("roomnumber", roomNumber);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            mSocket.emit("joinRoom", entranceinfo); //서버쪽으로 이벤트 발생시키기
            //방 번호, username을 보냄
        }
    };

    //서버 방 입장
    private Emitter.Listener onNewUser = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject entranceinfo = new JSONObject();
            try{
                entranceinfo = (JSONObject)args[0];
                String eUser = entranceinfo.getString("user");
                int eRoom = entranceinfo.getInt("room");
                Toast.makeText(getApplicationContext(),eUser+"님께서"+eRoom+"방에 입장하셨습니다",Toast.LENGTH_SHORT).show();
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
    };

}
