package com.example.lenovo.project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.WindowManager;

public class welcome_activity extends AppCompatActivity {
    private SubApplication sub_application;
    //打开该欢迎界面，防止做任何事情
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //跳过欢迎界面
        if(sub_application.skip_welcome){
            Intent intent = new Intent(welcome_activity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            sub_application.skip_welcome=true;
            handler.sendEmptyMessageDelayed(123,2000);
        }

    }
    private  Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==123){
                Intent intent = new Intent(welcome_activity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                finish();
            }
        }

    };
}
