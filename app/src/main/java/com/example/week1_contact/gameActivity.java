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


import java.util.ArrayList;

public class gameActivity extends Activity {

    private String answer;
    private boolean Offense_Defense = false;        //true : 그리기 & false : 맞추기

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

        //서버로 부터 random 하게 제시어 받아오기
        answer = "apple";

        Intent intent = getIntent();
        String name = intent.getExtras().getString("name");

        TextView textView = (TextView)findViewById(R.id.playerName);
        textView.setText(name);

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
}
