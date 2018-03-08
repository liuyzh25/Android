package com.example.lenovo.project;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView alarm,password_db,weather,memory,face,translation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarm=(TextView)findViewById(R.id.alarm);
        password_db=(TextView)findViewById(R.id.password_db);
        memory=(TextView) findViewById(R.id.memory);
        weather=(TextView)findViewById(R.id.weather);
        face=(TextView)findViewById(R.id.faceRecognition);
        translation=findViewById(R.id.translate);
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(MainActivity.this,alarmList_Activity.class);
                startActivity(intent1);
            }
        });
        password_db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2=new Intent(MainActivity.this,passwordActivity.class);
                startActivity(intent2);
            }
        });
        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3=new Intent(MainActivity.this,weather_activity.class);
                startActivity(intent3);
            }
        });
        memory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 =new Intent(MainActivity.this,memoList_activity.class);
                startActivity(intent4);

            }
        });
        face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent5 =new Intent(MainActivity.this,faceRecognition.class);
                startActivity(intent5);
            }
        });
        translation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent6 =new Intent(MainActivity.this,translate_activity.class);
                startActivity(intent6);
            }
        });
    }
}
