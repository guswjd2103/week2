package com.example.week1_contact;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import io.socket.client.IO;
import io.socket.client.Socket;

public class gameActivity extends Activity {

    private String TAG = "GameActivity";
    private Socket mSocket;
    private String answer;
    private int status = 0;        //0:일반 1:그림그리기 2:맞추기
    String userName;
    String roomName;

    private Button answerButton;
    private EditText answer_u;
    private TextView tvMain;
    private List<String> chatStrings= new ArrayList<String>();

    private ArrayList<String> problems = new ArrayList<String>(
            Arrays.asList("돈다발", "철학", "카레이서", "삼국시대", "가격표", "카카오나무", "가라오케", "가로수", "열매", "자선냄비", "사과", "소방관", "김경호", "산모", "티눈", "파인애플", "포옹",
                    "적발", "원빈", "팔걸이", "작은북", "수표", "이판사판", "강아지풀", "정전")
    );

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
            if(status != 1) return false;
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

        //design
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int heigth = dm.heightPixels;
        //chat layout 중첩시키기
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout chatLayout = (LinearLayout)layoutInflater.inflate(R.layout.chat,null);
        chatLayout.setBackgroundColor(Color.parseColor("#1A000000"));

        LinearLayout.LayoutParams param_chat = new LinearLayout.LayoutParams(width,heigth*6/40);
        param_chat.setMargins(0,heigth*87/120,0,0);
        addContentView(chatLayout,param_chat);

        //room head
        LinearLayout headLayout = (LinearLayout) findViewById(R.id.roomhead);
        LinearLayout.LayoutParams param_head = new LinearLayout.LayoutParams(width, heigth/12);
        param_head.setMargins(0,0,0,0);
        headLayout.setLayoutParams(param_head);

        //canvas
        drawlinear = findViewById(R.id.drawCanvas);
        LinearLayout.LayoutParams param_draw = new LinearLayout.LayoutParams(width,heigth*23/40);
        param_draw.setMargins(0,0,0,0);
        drawlinear.setLayoutParams(param_draw);

        //tool
        LinearLayout toolLayout = (LinearLayout)findViewById(R.id.gametool);
        LinearLayout.LayoutParams param_tool = new LinearLayout.LayoutParams(width, heigth/15);
        param_tool.setMargins(0,0,0,0);
        toolLayout.setLayoutParams(param_tool);

        //tell
        LinearLayout tellLayout = (LinearLayout)findViewById(R.id.gametell);
        LinearLayout.LayoutParams param_tell = new LinearLayout.LayoutParams(width, heigth/12);
        param_tell.setMargins(0,heigth*6/40,0,0);
        tellLayout.setLayoutParams(param_tell);


        //
        answer = "qwerasdfzxcv";
        chatStrings.add("");
        chatStrings.add("");
        chatStrings.add("");
        chatStrings.add("");
        chatStrings.add("");
        chatStrings.add("");

        problems.add("apple");

        Intent intent = getIntent();
        userName = intent.getExtras().getString("name");
        roomName = intent.getExtras().getString("roomName");

        TextView textView = (TextView)findViewById(R.id.roomName);
        textView.setText(roomName);

        final MyView m = new MyView(this);
        drawlinear.addView(m);
        tvMain = (TextView)findViewById(R.id.tvMain);

        TextView problem_text = (TextView)findViewById(R.id.problem_text);
        Button blackButton = (Button)findViewById(R.id.blackButton);
        Button redButton = (Button)findViewById(R.id.redButton);
        Button blueButton = (Button)findViewById(R.id.blueButton);
        Button yellowButton = (Button)findViewById(R.id.yellowButton);
        Button greenButton = (Button)findViewById(R.id.greenButton);
        Button orchidButton =(Button)findViewById(R.id.orchidButton);
        Button grayButton = (Button)findViewById(R.id.grayButton);
        Button eraserButton = (Button)findViewById(R.id.eraserButton);
        Button resetButton = (Button)findViewById(R.id.resetButton);
        answer_u = (EditText)findViewById(R.id.answer);
        answerButton = (Button)findViewById(R.id.answerButton);

        problem_text.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(status != 0) return;
                status = 1;
                answer_u.setEnabled(false);
                Random r = new Random();
                int i = r.nextInt(problems.size());
                answer = problems.get(i);
                problem_text.setText("    "+answer);
                JsonObject problemObject = new JsonObject();
                problemObject.addProperty("problem", answer + "");
                problemObject.addProperty("roomName", roomName + "");
                JSONObject jsonObject = null;

                try{
                    jsonObject =  new JSONObject(problemObject.toString());
                } catch (JSONException e){
                    e.printStackTrace();
                }

                mSocket.emit("problem", jsonObject);

            }
        });
        blackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { color = Color.BLACK; }
        });
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
        greenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color = Color.GREEN;
            }
        });
        orchidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color = Color.parseColor("#DA70D6");
            }
        });
        grayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color = Color.GRAY;
            }
        });
        eraserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color = Color.WHITE;
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //reset하면 RESET했다고 TOAST띄워주기
                if(status != 1) return;
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
                if(status == 1) return;
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
                    Toast.makeText(getApplicationContext(), userName +  "님 Correct!!", Toast.LENGTH_SHORT); // 권한 넘겨주고 문제 새로 내기\
                    Log.d("correct answer", roomName);
                    JsonObject resetObject2 = new JsonObject();
                    resetObject2.addProperty("userName", userName + "");
                    resetObject2.addProperty("roomName", roomName+ "");
                    JSONObject jsonObject3 = null;

                    try{
                        jsonObject3 =  new JSONObject(resetObject2.toString());
                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                    mSocket.emit("reset", jsonObject3);

                    status = 1;
                    answer_u.setEnabled(false);
                    Random r = new Random();
                    int i = r.nextInt(problems.size());
                    answer = problems.get(i);
                    problem_text.setText(answer+"        ");
                    JsonObject problemObject = new JsonObject();
                    problemObject.addProperty("problem", answer + "");
                    problemObject.addProperty("roomName", roomName + "");
                    JSONObject jsonObject2 = null;

                    try{
                        jsonObject2 =  new JSONObject(problemObject.toString());
                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                    mSocket.emit("problem", jsonObject2);

                }
                else{
//                    Toast.makeText(getApplicationContext(), "InCorrect", Toast.LENGTH_SHORT).show();
                }
                answer_u.setText("");

                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(answer_u.getWindowToken(),0);
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
                    String chatString = "";
                    chatStrings.add(jsonObject.get("answer").getAsString());
                    int chs = chatStrings.size();
                    for(int i = 6; i>0; i--){
                        chatString = chatString + chatStrings.get(chs-i);
                    }
                    tvMain.setText(chatString);
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
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            final Toast toast = Toast.makeText(getApplicationContext(), userName +  "님께서 " + " 그림판을 reset 했습니다", Toast.LENGTH_SHORT);
//                            int offsetX = 0;
//                            int offsetY = 0;
//                            toast.setGravity(Gravity.CENTER, offsetX, offsetY);
//                            toast.show();
//                        }
//                    });
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }).on("leaveMsg", (Object... objects) -> {
                JsonParser jsonParsers = new JsonParser();
                JsonObject leaveinfo = (JsonObject) jsonParsers.parse(objects[0] + "");
                Log.d("leaveinfo", leaveinfo.toString());
                try{
                    String userName = leaveinfo.get("userName").getAsString();
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
            }).on("receiveProblem",(Object... objects) -> { //다른사람은 false로 하고 answer도 보내줘야함
                JsonParser jsonParsers = new JsonParser();
                JsonObject probleminfo = (JsonObject) jsonParsers.parse(objects[0] + "");
                Log.d("probleminfo", probleminfo.toString());
                try{
                    String problem = probleminfo.get("answer").getAsString();
                    status = 2;
                    answer_u.setEnabled(true);
                    answer = problem;
                    runOnUiThread(new Runnable() {
                        public void run() {
                            problem_text.setText("????        ");
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
