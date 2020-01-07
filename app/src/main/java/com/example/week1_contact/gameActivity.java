package com.example.week1_contact;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    String roomName;

    private Button answerButton;
    private EditText answer_u;
    private TextView tvMain;

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

        public float getX() {
            return this.x;
        }

        public float getY() {
            return this.y;
        }

        public boolean getCheck() {
            return this.check;
        }

        public int getColor() {
            return this.color;
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

        @Override //그리기
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN :
                    Point addPoint = new Point(x,y,false, color);
                    points.add(addPoint);
                    sendDrawSocket(addPoint);
                    break;
                case MotionEvent.ACTION_MOVE :
                    Point addPoint2 = new Point(x,y,true, color);
                    points.add(addPoint2);
                    sendDrawSocket(addPoint2);
                    Log.d(TAG, "좌표 : x:"+x+" / y:"+y);
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

        //서버로 부터 random 하게 제시어 받아오기
        answer = "apple";

        Intent intent = getIntent();
        userName = intent.getExtras().getString("name");
        roomName = intent.getExtras().getString("roomName");

        Log.d("roonName from fragment", roomName);

        TextView textView = (TextView)findViewById(R.id.playerName);
        textView.setText(roomName);

        final MyView m = new MyView(this);
        drawlinear = findViewById(R.id.drawCanvas);
        drawlinear.addView(m);

        answer_u = (EditText)findViewById(R.id.answer);
        tvMain = (TextView)findViewById(R.id.tvMain);

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
            public void onClick(View v) { //reset하면 RESET했다고 TOAST띄워주기
                JsonObject resetObject = new JsonObject();
                resetObject.addProperty("userName", userName + "");
                resetObject.addProperty("roomName", roomName+"");
                JSONObject jsonObject = null;

                try{
                    jsonObject =  new JSONObject(resetObject.toString());
                } catch (JSONException e){
                    e.printStackTrace();
                }

                mSocket.emit("reset", jsonObject);

            }
        });

        answerButton = (Button)findViewById(R.id.answerButton);
        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonObject answerObject = new JsonObject();
                answerObject.addProperty("answer", answer_u.getText() + "");
                answerObject.addProperty("userName", userName + "");
                answerObject.addProperty("roomName", roomName +"");
                JSONObject jsonObject = null;

                try{
                    jsonObject =  new JSONObject(answerObject.toString());
                } catch (JSONException e){
                    e.printStackTrace();
                }

                mSocket.emit("reqMsg", jsonObject);

                if(answer.equals(answer_u.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "InCorrect", Toast.LENGTH_SHORT).show();
                }
                answer_u.setText("");

                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(answer_u.getWindowToken(),0);
                //정답과 비교해서 턴을 넘겨주는?
//                if(Offense_Defense == true) return;
            }
        });

        try {
            mSocket = IO.socket("http://192.249.19.251:0280");
            mSocket.on(Socket.EVENT_CONNECT, (Object... objects) -> {
                JsonObject enterObject = new JsonObject();
                enterObject.addProperty("roomName", roomName);
                enterObject.addProperty("username", userName);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(enterObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSocket.emit("joinRoom",jsonObject);
            }).on("recMsg", (Object... objects) -> {
                JsonParser jsonParsers = new JsonParser();
                JsonObject jsonObject = (JsonObject) jsonParsers.parse(objects[0] + "");
                runOnUiThread(()->{
                    tvMain.setText(tvMain.getText().toString()+jsonObject.get("answer").getAsString());
                });
            }).on("newUser", (Object... objects) -> {
                Log.d("newUsersocket", "enternewUser socket");
                JsonParser jsonParsers = new JsonParser();
                JsonObject entranceinfo = (JsonObject) jsonParsers.parse(objects[0] + "");
                Log.d("enteranceInfo", entranceinfo.toString());
                try{
                    String eUser = entranceinfo.get("username").getAsString();
                    String eRoom = entranceinfo.get("roomName").getAsString();
                    Log.d("roomName in socket", eRoom);
                    Log.d("roomName", roomName);
                    if(eRoom.equals(roomName)) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                final Toast toast = Toast.makeText(getApplicationContext(), eUser +  "님께서 " + eRoom + " 방에 입장하셨습니다", Toast.LENGTH_SHORT);
                                int offsetX = 0;
                                int offsetY = 0;
                                toast.setGravity(Gravity.CENTER, offsetX, offsetY);
                                toast.show();
                            }
                        });
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }).on("paint",(Object... objects) -> {
                Log.d("paintSocket", "point android socket");
                JsonParser jsonParsers = new JsonParser();
                JsonObject paintinfo = (JsonObject) jsonParsers.parse(objects[0] + "");
                Log.d("paintinfo", paintinfo.toString());
                try{
                    Float x = paintinfo.get("x").getAsFloat();
                    Float y = paintinfo.get("y").getAsFloat();
                    boolean check = paintinfo.get("check").getAsBoolean();
                    int color = paintinfo.get("color").getAsInt();
                    Log.d("x", Float.toString(x));
                    Log.d("y", Float.toString(y));

                    Point receivedPoint = new Point(x,y, check, color);
                    points.add(receivedPoint);
                    m.invalidate();

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }).on("resetPaint",(Object... objects) -> {
                JsonParser jsonParsers = new JsonParser();
                JsonObject resetinfo = (JsonObject) jsonParsers.parse(objects[0] + "");
                Log.d("resetinfo", resetinfo.toString());
                try{
                    String userName = resetinfo.get("resetMsg").getAsString();
                    points.clear();
                    m.invalidate();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            final Toast toast = Toast.makeText(getApplicationContext(), userName +  "님께서 " + " 그림판을 reset 했습니다", Toast.LENGTH_SHORT);
                            int offsetX = 0;
                            int offsetY = 0;
                            toast.setGravity(Gravity.CENTER, offsetX, offsetY);
                            toast.show();
                        }
                    });
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }).on("leaveMsg", (Object... objects) -> {
                JsonParser jsonParsers = new JsonParser();
                JsonObject leaveinfo = (JsonObject) jsonParsers.parse(objects[0] + "");
                Log.d("leaveinfo", leaveinfo.toString());
                try{
                    String userName = leaveinfo.get("username").getAsString();
                    String leaveRoom = leaveinfo.get("roomName").getAsString();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            final Toast toast = Toast.makeText(getApplicationContext(), userName +  "님께서 " +  leaveRoom + " 방을 나갔습니다", Toast.LENGTH_SHORT);
                            int offsetX = 0;
                            int offsetY = 0;
                            toast.setGravity(Gravity.CENTER, offsetX, offsetY);
                            toast.show();
                        }
                    });
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            });
            mSocket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendDrawSocket(Point point){
//        Log.d("socket","send point function");

        JsonObject pointObject = new JsonObject();
        pointObject.addProperty("x", point.getX());
        pointObject.addProperty("y", point.getY());
        pointObject.addProperty("check", point.getCheck());
        pointObject.addProperty("color", point.getColor());
        pointObject.addProperty("roomName", roomName+"");
        JSONObject jsonObject = null;

        try{
            jsonObject =  new JSONObject(pointObject.toString());
        } catch (JSONException e){
            e.printStackTrace();

        }

        mSocket.emit("draw", jsonObject);
    }

    @Override
    public void onBackPressed() {
        JsonObject leaveObject = new JsonObject();
        leaveObject.addProperty("roomName", roomName + "");
        leaveObject.addProperty("userName", userName + "");
        JSONObject jsonObject = null;

        try{
            jsonObject =  new JSONObject(leaveObject.toString());
        } catch (JSONException e){
            e.printStackTrace();

        }

        mSocket.emit("leave", jsonObject);
        super.onBackPressed();
    }

    @Override
    public void onStop() {
        JsonObject leaveObject = new JsonObject();
        leaveObject.addProperty("roomName", roomName + "");
        leaveObject.addProperty("userName", userName + "");
        JSONObject jsonObject = null;

        try{
            jsonObject =  new JSONObject(leaveObject.toString());
        } catch (JSONException e){
            e.printStackTrace();

        }

        mSocket.emit("leave", jsonObject);
        super.onStop();
    }
}
