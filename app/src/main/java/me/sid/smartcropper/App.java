package me.sid.smartcropper;

import android.app.Activity;
import android.app.Application;

import me.sid.smartcropperlib.SmartCropper;

public class App extends Application {
    Activity mCurrentActivity;
    public static App INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        // 如果使用机器学习代替 Canny 算子，请初始化 ImageDetector
        SmartCropper.buildImageDetector(this);
    }
    public static synchronized App getInstance(){
        return INSTANCE;
    }

    public void setmCurrentActivity(Activity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

    public Activity getmCurrentActivity() {
        return mCurrentActivity;
    }
}
