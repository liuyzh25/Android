package com.example.lenovo.project;
import android.app.Application;
public class SubApplication extends Application {
    public static Boolean skip_welcome;
    @Override
    public void onCreate() {
        skip_welcome=false;
        super.onCreate();
    }
}
