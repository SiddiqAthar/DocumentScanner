package me.sid.smartcropper.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharePrefData {
    private static final SharePrefData instance = new SharePrefData();
    private static final String INTRO_DONE = "intro_done";
    private static final String ADS_PREFS = "adprefs", IS_ADMOB_SPLASH_DOC="splashdoc"
           ,IS_ADMOB_FOLDER_DOC="folderdoc"
            , IS_ADMOB_MAIN="main", IS_ADMOB_SETTING="settig", IS_ADMOB_PDF_INTER="pdfinter", IS_ADMOB_SPLASH_INTER="splashinter";

    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    public static synchronized SharePrefData getInstance() {
        return instance;
    }
    private Context context;
    public void setContext(Context context) {
        this.sp = PreferenceManager.getDefaultSharedPreferences(context);
        this.context=context;
    }

    public SharePrefData(Context context) {
        this.sp = PreferenceManager.getDefaultSharedPreferences(context);
        this.context=context;
    }

    private SharePrefData() {
    }


    public void setIntroScrenVisibility(boolean intro) {

        spEditor = sp.edit();
        spEditor.putBoolean(INTRO_DONE, intro);
        spEditor.apply();
        spEditor.commit();
    }

    public boolean getIntroScreenVisibility() {


        return sp.getBoolean(INTRO_DONE, false);
    }


    public Boolean getADS_PREFS() {
        return sp.getBoolean(ADS_PREFS, false);
    }

    public void setADS_PREFS(Boolean ADS_PREFS_) {
        spEditor = sp.edit();
        spEditor.putBoolean(ADS_PREFS, ADS_PREFS_);
        spEditor.apply();
        spEditor.commit();
    }


    @SuppressLint("CommitPrefEdits")
    public boolean destroyUserSession() {
        this.spEditor = this.sp.edit();
//        this.spEditor.remove(LOGGED_IN);
        this.spEditor.apply();
        return true;
    }

    public void setIsAdmobSplashInter(String isadmob) {
        spEditor = sp.edit();
        spEditor.putString(IS_ADMOB_SPLASH_INTER, isadmob);
        spEditor.apply();
        spEditor.commit();
    }
    public String getIsAdmobSplashInter() {
        return sp.getString(IS_ADMOB_SPLASH_INTER, "true");
    }

    public void setIsAdmobPdfInter(String isadmob) {
        spEditor = sp.edit();
        spEditor.putString(IS_ADMOB_PDF_INTER, isadmob);
        spEditor.apply();
        spEditor.commit();
    }
    public String getIsAdmobPdfInter() {
        return sp.getString(IS_ADMOB_PDF_INTER, "true");
    }

    public void setIsAdmobSplashDoc(String isadmob) {
        spEditor = sp.edit();
        spEditor.putString(IS_ADMOB_SPLASH_DOC, isadmob);
        spEditor.apply();
        spEditor.commit();
    }
    public String getIsAdmobSplashDoc() {
        return sp.getString(IS_ADMOB_SPLASH_DOC, "true");
    }


    public String getIsAdmobFolderDoc() {
        return sp.getString(IS_ADMOB_FOLDER_DOC, "true");
    }

    public void setIsAdmobFolderDoc(String isadmob) {
        spEditor = sp.edit();
        spEditor.putString(IS_ADMOB_FOLDER_DOC, isadmob);
        spEditor.apply();
        spEditor.commit();
    }


    public String getIsAdmobMain() {
        return sp.getString(IS_ADMOB_MAIN, "true");
    }

    public void setIsAdmobMain(String isadmob) {
        spEditor = sp.edit();
        spEditor.putString(IS_ADMOB_MAIN, isadmob);
        spEditor.apply();
        spEditor.commit();
    }

    public String getIsAdmobSetting() {
        return sp.getString(IS_ADMOB_SETTING, "true");
    }

    public void setIsAdmobSetting(String isadmob) {
        spEditor = sp.edit();
        spEditor.putString(IS_ADMOB_SETTING, isadmob);
        spEditor.apply();
        spEditor.commit();
    }

}
