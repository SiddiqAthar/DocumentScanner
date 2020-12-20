package me.sid.smartcropper;

import android.app.Activity;
import android.app.Application;

import me.sid.smartcropper.utils.SharePrefData;
import me.sid.smartcropperlib.SmartCropper;

public class App extends Application {
    Activity mCurrentActivity;
    public static App INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE=this;
        SharePrefData.getInstance().setContext(getApplicationContext());
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
