package me.sid.smartcropper.views.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ShareCompat;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
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

import java.util.ArrayList;
import java.util.List;

import me.sid.smartcropper.R;
import me.sid.smartcropper.adapters.AllFilesAdapter;
import me.sid.smartcropper.adapters.SettingsAdapter;
import me.sid.smartcropper.dialogs.AlertDialogHelper;
import me.sid.smartcropper.interfaces.GenericCallback;
import me.sid.smartcropper.models.SettingsData;
import me.sid.smartcropper.utils.SharePrefData;

public class SettingActivity extends BaseActivity implements View.OnClickListener, GenericCallback, BillingProcessor.IBillingHandler {

    ImageButton setting_back_btn, setting_exit_btn;
    RecyclerView recyclerView_setting;
    SettingsAdapter settingsAdapter;
    ArrayList<SettingsData> settingsData = new ArrayList<>();

    FrameLayout admobNativeView;
    NativeAdLayout nativeAdContainer;
    ConstraintLayout adlayout;
    View adlayout2;
    BillingProcessor billingHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();
    }

    private void init() {
        billingHandler =
                BillingProcessor.newBillingProcessor(this, getString(R.string.billing_id), this);
        billingHandler.initialize();

        setting_back_btn = findViewById(R.id.setting_back_btn);
        setting_back_btn.setOnClickListener(this);
        setting_exit_btn = findViewById(R.id.setting_exit_btn);
        setting_exit_btn.setOnClickListener(this);

        recyclerView_setting = findViewById(R.id.recyclerView_setting);
        recyclerView_setting.setLayoutManager(new LinearLayoutManager(this));

        settingsData.add(new SettingsData(R.drawable.ic_upgrade_s, getString(R.string.premium), R.drawable.ic_yellow_ic));
        settingsData.add(new SettingsData(R.drawable.ic_contact_s, getString(R.string.contact_us), R.drawable.ic_arrow_s));
        settingsData.add(new SettingsData(R.drawable.ic_trash, getString(R.string.trash), R.drawable.ic_arrow_s));
        settingsData.add(new SettingsData(R.drawable.ic_privacy_s, getString(R.string.policy), R.drawable.ic_arrow_s));
        settingsData.add(new SettingsData(R.drawable.ic_help_s, getString(R.string.how_to_use), R.drawable.ic_arrow_s));
        settingsData.add(new SettingsData(R.drawable.ic_rate_ic, getString(R.string.rate), R.drawable.ic_arrow_s));
        settingsData.add(new SettingsData(R.drawable.ic_share_s, getString(R.string.share_app), R.drawable.ic_arrow_s));

        settingsAdapter = new SettingsAdapter(settingsData, SettingActivity.this, this);
        recyclerView_setting.setAdapter(settingsAdapter);

        adlayout2=findViewById(R.id.adlayout2);
        admobNativeView = findViewById(R.id.admobNativeView);
        nativeAdContainer = findViewById(R.id.native_ad_container);
        adlayout=findViewById(R.id.adlayout);

        if(SharePrefData.getInstance().getIsAdmobSetting().equals("true") && !SharePrefData.getInstance().getADS_PREFS()){
            loadAdmobNativeAd();
        }else if(SharePrefData.getInstance().getIsAdmobSetting().equals("false") && !SharePrefData.getInstance().getADS_PREFS()){
            loadNativeAd();
        }else{
            admobNativeView.setVisibility(View.GONE);
            nativeAdContainer.setVisibility(View.GONE);
            adlayout.setVisibility(View.GONE);
            adlayout2.setVisibility(View.GONE);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.setting_back_btn) {
            finish();
        } else if (view.getId() == R.id.setting_exit_btn) {
            quitApp(this);
        }
    }

    @Override
    public void callback(Object o) {

        final String whereTo = (String) o;

        if (whereTo.equals(getResources().getString(R.string.rate))) {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
            try {
                startActivity(myAppLinkToMarket);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(SettingActivity.this, " unable to find market app", Toast.LENGTH_LONG).show();
            }
        } else if (whereTo.equals(getResources().getString(R.string.share_app))) {
            ShareCompat.IntentBuilder.from(SettingActivity.this)
                    .setType("text/plain")
                    .setChooserTitle("Share App")
                    .setText("http://play.google.com/store/apps/details?id=" + getPackageName()).startChooser();
        }
        else if (whereTo.equals(getResources().getString(R.string.trash))) {
            startActivity(TrashActivity.class, null);
        } else if(whereTo.equals(getString(R.string.premium))){
            billingHandler.purchase(this, getString(R.string.product_key));
        } else if(whereTo.equals(getString(R.string.policy))){
            startActivity(new Intent(this,PrivacyActivity.class));
        }
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
                adlayout2.setVisibility(View.GONE);
//                loadAdmobNativeAd();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (fbNativead == null || fbNativead != ad) {
                    return;
                }

                adlayout2.setVisibility(View.GONE);
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
                (ConstraintLayout) getLayoutInflater().inflate(R.layout.home_fb_native, fbNativeAdlayout, false);
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

                if (nativeAd != null) {
                    nativeAd.destroy();
                }


                adlayout2.setVisibility(View.GONE);
                admobNativeView.setVisibility(View.VISIBLE);
                nativeAdContainer.setVisibility(View.GONE);
                nativeAd = unifiedNativeAd;
                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater().inflate(R.layout.home_admob_native, null);
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


    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        SharePrefData.getInstance().setADS_PREFS(true);
        Intent intent = new Intent(this, SplashActivity.class);
        this.startActivity(intent);
        this.finishAffinity();
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (!billingHandler.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}