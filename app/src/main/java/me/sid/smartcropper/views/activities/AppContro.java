package me.sid.smartcropper.views.activities;

import android.app.Activity;
import android.app.Application;

public class AppContro extends Application {
    Activity mCurrentActivity;
    public static AppContro INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE=this;
     //   SharePrefData.getInstance().setContext(getApplicationContext());
    }

    public static synchronized AppContro getInstance(){
        return INSTANCE;
    }

    public void setmCurrentActivity(Activity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

    public Activity getmCurrentActivity() {
        return mCurrentActivity;
    }
}

