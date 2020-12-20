package me.sid.smartcropper.views.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.sid.smartcropper.App;
import me.sid.smartcropper.BuildConfig;
import me.sid.smartcropper.R;
import me.sid.smartcropper.interfaces.PermissionCallback;
import me.sid.smartcropper.utils.Constants;
import me.sid.smartcropper.utils.InterstitalAdsInner;
import me.sid.smartcropper.utils.PermissionUtils;
import me.sid.smartcropper.utils.SharePrefData;
import pub.devrel.easypermissions.EasyPermissions;

import static me.sid.smartcropper.utils.PermissionUtils.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS;

public class SplashActivity extends BaseActivity {
    TextView tv1;
    String[] array = new String[3];
    int PHONE_STATE_CODE = 101;
    FirebaseRemoteConfig remoteConfig;
    FrameLayout admobNativeView;
    NativeAdLayout nativeAdContainer;
    View adlayout;
    Button continueBtn;
    CheckBox checkBox;
    TextView privacyText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        array[0] = Manifest.permission.READ_EXTERNAL_STORAGE;
        array[1] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        array[2] = Manifest.permission.CAMERA;

        admobNativeView = findViewById(R.id.admobNativeView);
        nativeAdContainer = findViewById(R.id.native_ad_container);
        adlayout=findViewById(R.id.adlayout);

        setUpRemoteConfig();

        continueBtn = findViewById(R.id.continueBtn);
        checkBox = findViewById(R.id.checkboxnew);
        privacyText = findViewById(R.id.privacytext);

        if (SharePrefData.getInstance().getIntroScreenVisibility()) {
            checkBox.setChecked(true);
//            continueBtn.setEnabled(true);
        } else {
            checkBox.setChecked(false);
//            continueBtn.setEnabled(false);
        }

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {
                    SharePrefData.getInstance().setIntroScrenVisibility(true);
                    if (PermissionUtils.hasPermissionGranted(
                            SplashActivity.this,
                            array
                    )
                    ) {

                        InterstitalAdsInner ads=new InterstitalAdsInner();
                        Intent intent = new Intent(SplashActivity.this, GernalCameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        ads.adMobShoeClose(SplashActivity.this, intent);

                        finish();

                    } else {

                        requestRequiredPermissions();
                    }

                }else{
                    showToast("Please check our Privacy Policy in order to continue",SplashActivity.this);
                }
            }
        });



        privacyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashActivity.this, PrivacyActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        String text = "I Accept the Privacy Policy";

        Spannable spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorBlue)),
                13, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        privacyText.setText(spannable, TextView.BufferType.SPANNABLE);
        tv1 = findViewById(R.id.tv1);
        Spannable docScannerSpan = new SpannableString("DOC SCANNER");
        docScannerSpan.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.sea_green)), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv1.setText(docScannerSpan);



    }

    public void requestRequiredPermissions() {
        PermissionUtils.checkAndRequestPermissions(
                this,
                array,
                PHONE_STATE_CODE
        );
    }

    private void goToCamera() {
        InterstitalAdsInner ads=new InterstitalAdsInner();
        Intent intent = new Intent(SplashActivity.this, GernalCameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ads.adMobShoeClose(SplashActivity.this, intent);

        finish();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == 23) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Displaying a toast
                Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Permission Needed To Run The App", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == PHONE_STATE_CODE) {
            Map<String, Integer> perms = new HashMap<String, Integer>();

            perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);

            for (int i = 0; i < permissions.length; i++)
                perms.put(permissions[i], grantResults[i]);
            if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                goToCamera();

            } else {
                // Permission Denied
                Toast.makeText(SplashActivity.this, "Permission Needed To Run The App", Toast.LENGTH_SHORT)
                        .show();

                finish();
            }

        }
    }

    private void setUpRemoteConfig() {

        remoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(1)
                .build();

        remoteConfig.setConfigSettingsAsync(configSettings);

        remoteConfig.setDefaultsAsync(R.xml.remoteapp);

        remoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {

                if (task.isSuccessful()&& !BuildConfig.DEBUG) {

                    remoteConfig.activate();
                    FirebaseRemoteConfig.getInstance().activate();
                    remoteConfig.activate();

                    SharePrefData.getInstance().setIsAdmobSplashDoc(remoteConfig.getString("isadmobsplashDoc"));
                    SharePrefData.getInstance().setIsAdmobMain(remoteConfig.getString("isadmobmain"));
                    SharePrefData.getInstance().setIsAdmobSetting(remoteConfig.getString("isadmobsetting"));
                    SharePrefData.getInstance().setIsAdmobFolderDoc(remoteConfig.getString("isadmobfolderdoc"));

                } else {
                    SharePrefData.getInstance().setIsAdmobSplashDoc("true");
                    SharePrefData.getInstance().setIsAdmobMain("true");
                    SharePrefData.getInstance().setIsAdmobSetting("true");
                    SharePrefData.getInstance().setIsAdmobFolderDoc("true");

                }

                InterstitalAdsInner.Companion.loadInterstitialAd(SplashActivity.this);

                if(SharePrefData.getInstance().getIsAdmobSplashDoc().equals("true") && !SharePrefData.getInstance().getADS_PREFS()){
                    Log.d("adloaded","admob check");
                    loadAdmobNativeAd();

                }else if(SharePrefData.getInstance().getIsAdmobSplashDoc().equals("false") && !SharePrefData.getInstance().getADS_PREFS()){
                    Log.d("adloaded","facebook check");
                    loadNativeAd();
                }else{
                    Log.d("adloaded","no check");
                    admobNativeView.setVisibility(View.GONE);
                    nativeAdContainer.setVisibility(View.GONE);
                    adlayout.setVisibility(View.GONE);
                }
            }
        });
    }

    NativeAd fbNativead;
    NativeAdLayout fbNativeAdlayout;
    ConstraintLayout fbAdview;

    private void loadNativeAd() {

        fbNativead = new NativeAd(this, getString(R.string.fb_native));

        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
            }

            @Override
            public void onError(Ad ad, AdError adError) {

//                binding.admobNativeView.setVisibility(View.VISIBLE);
                nativeAdContainer.setVisibility(View.GONE);
                admobNativeView.setVisibility(View.GONE);
                adlayout.setVisibility(View.GONE);
//                loadAdmobNativeAd();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d("adloaded","facebook");
                if (fbNativead == null || fbNativead != ad) {
                    return;
                }
                adlayout.setVisibility(View.GONE);
                admobNativeView.setVisibility(View.GONE);
                nativeAdContainer.setVisibility(View.VISIBLE);
                // Inflate Native Ad into Container
                inflateAd(fbNativead);
            }

            @Override
            public void onAdClicked(Ad ad) {
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        };

        fbNativead.loadAd(
                fbNativead.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build());
    }


    private void inflateAd(NativeAd nativeAd) {
        nativeAd.unregisterView();
        fbNativeAdlayout = findViewById(R.id.native_ad_container);
        fbAdview =
                (ConstraintLayout) getLayoutInflater().inflate(R.layout.loading_fb_native, fbNativeAdlayout, false);
        fbNativeAdlayout.addView(fbAdview);

        LinearLayout adChoicesContainer = fbAdview.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(this, nativeAd, fbNativeAdlayout);
        adOptionsView.setIconColor(Color.parseColor("#271337"));
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);


        com.facebook.ads.MediaView nativeAdIcon = fbAdview.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = fbAdview.findViewById(R.id.native_ad_title);
        TextView nativeAdBody = fbAdview.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = fbAdview.findViewById(R.id.native_ad_sponsored_label);
        TextView nativeAdSocialContext = fbAdview.findViewById(R.id.native_ad_social_context);
        Button nativeAdCallToAction = fbAdview.findViewById(R.id.native_ad_call_to_action);
        ConstraintLayout contentsFb = fbAdview.findViewById(R.id.contentfb);

        com.facebook.ads.MediaView nativeAdMedia = fbAdview.findViewById(R.id.native_ad_media);

        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        if (nativeAd.hasCallToAction()) {
            nativeAdCallToAction.setVisibility(View.VISIBLE);
        } else {
            nativeAdCallToAction.setVisibility(View.INVISIBLE);
        }
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        List<View> clickableViews = new ArrayList<>();

        clickableViews.add(nativeAdCallToAction);


        nativeAd.registerViewForInteraction(
                fbAdview,
                nativeAdMedia,
                nativeAdIcon,
                clickableViews
        );


    }


    UnifiedNativeAd nativeAd;

    private void loadAdmobNativeAd() {


        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.admob_native));

        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {

                Log.d("adloaded","admob");
                if (nativeAd != null) {
                    nativeAd.destroy();
                }

                adlayout.setVisibility(View.GONE);
                admobNativeView.setVisibility(View.VISIBLE);
                nativeAdContainer.setVisibility(View.GONE);
                nativeAd = unifiedNativeAd;
                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater().inflate(R.layout.loading_admob_native, null);
                populateUnifiedNativeAdView(nativeAd, adView);
                admobNativeView.removeAllViews();
                admobNativeView.addView(adView);

            }


        });


        AdLoader adLoader = builder.build();
        adLoader.loadAd(new AdRequest.Builder().build());


    }


    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        mediaView.setImageScaleType(ImageView.ScaleType.FIT_XY);
        adView.setMediaView(mediaView);
        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        /* adView.setPriceView(adView.findViewById(R.id.ad_price))
         adView.setStarRatingView(adView.findViewById(R.id.ad_stars))
         adView.setStoreView(adView.findViewById(R.id.ad_store))
         adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser))*/
        // The headline is guaranteed to be in every UnifiedNativeAd.

        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }
        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable()
            );
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);

    }


}