package me.sid.smartcropper.views.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import me.sid.smartcropper.R;
import me.sid.smartcropper.adapters.AllFilesAdapter;
import me.sid.smartcropper.models.FileInfoModel;
import me.sid.smartcropper.utils.BannerAds;
import me.sid.smartcropper.utils.DirectoryUtils;
import me.sid.smartcropper.utils.FileUtils;
import me.sid.smartcropper.utils.RealPathUtil;
import me.sid.smartcropper.utils.SharePrefData;
import me.sid.smartcropper.utils.StringUtils;

import static me.sid.smartcropper.utils.Constants.mFileSelectCode;


public class AllFilesInFolderActivity extends BaseActivity implements View.OnClickListener {
    File folderFile;
    Toolbar toolbar;
    RecyclerView allFilesRecycler;
    DirectoryUtils mDirectoryUtils;
    RelativeLayout noFileLayout;
    AllFilesAdapter adapter;
    ArrayList<FileInfoModel> fileInfoModelArrayList;
    Button importFilesBtn;
    FileUtils fileUtils;
    TextView filterTv, emptyView;

    FrameLayout admobNativeView;
    NativeAdLayout nativeAdContainer;
    ConstraintLayout adlayout;
    View adlayout2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_files_in_folder);
        if (getIntent().getExtras() != null) {
            folderFile = new File(Objects.requireNonNull(getIntent().getStringExtra("path")));
        }
        fileInfoModelArrayList = new ArrayList<>();
        mDirectoryUtils = new DirectoryUtils(AllFilesInFolderActivity.this);
        fileUtils = new FileUtils(AllFilesInFolderActivity.this);
        noFileLayout = findViewById(R.id.noFileLayout);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(folderFile.getName());
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
//        importFilesBtn = findViewById(R.id.importFilesBtn);
//        importFilesBtn.setOnClickListener(this);
        filterTv = findViewById(R.id.filterTv);
        filterTv.setOnClickListener(this);
        emptyView = findViewById(R.id.empty_view);
        allFilesRecycler = findViewById(R.id.allFilesRecycler);
        allFilesRecycler.setLayoutManager(new LinearLayoutManager(AllFilesInFolderActivity.this));
        adapter = new AllFilesAdapter(AllFilesInFolderActivity.this, fileInfoModelArrayList);
        getFiles();
        importFilesBtn = findViewById(R.id.importFilesBtn);
        importFilesBtn.setOnClickListener(this);

        adlayout2=findViewById(R.id.adlayout2);
        admobNativeView = findViewById(R.id.admobNativeView);
        nativeAdContainer = findViewById(R.id.native_ad_container);
        adlayout=findViewById(R.id.adlayout);

//        if(SharePrefData.getInstance().getIsAdmobFolderDoc().equals("true") && !SharePrefData.getInstance().getADS_PREFS()){
//            loadAdmobNativeAd();
//        }else if(SharePrefData.getInstance().getIsAdmobFolderDoc().equals("false") && !SharePrefData.getInstance().getADS_PREFS()){
//            loadNativeAd();
//        }else{
//            admobNativeView.setVisibility(View.GONE);
//            nativeAdContainer.setVisibility(View.GONE);
//            adlayout.setVisibility(View.GONE);
//            adlayout2.setVisibility(View.GONE);
//        }
        RelativeLayout admobbanner=  (RelativeLayout)findViewById(R.id.admobBanner);
        if(SharePrefData.getInstance().getIsAdmobFolderDoc().equals("true") && !SharePrefData.getInstance().getADS_PREFS()){
            admobbanner.setVisibility(View.VISIBLE);
            BannerAds.Companion.loadAdmob(this,"large",admobbanner);
            adlayout2.setVisibility(View.GONE);
            adlayout.setBackground(null);
        }else if (SharePrefData.getInstance().getIsAdmobFolderDoc().equals("false") && !SharePrefData.getInstance().getADS_PREFS()) {
            admobbanner.setVisibility(View.GONE);
            loadNativeAd();
        } else {
            nativeAdContainer.setVisibility(View.GONE);
            adlayout.setVisibility(View.GONE);
            adlayout2.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.importFilesBtn) {
            Intent intent = fileUtils.getMultipleFileChooser();
            startActivityForResult(intent, mFileSelectCode);
        } else if (view.getId() == R.id.filterTv) {
            showSortMenu();
        }
    }

    private void showSortMenu() {
        Context wrapper = new ContextThemeWrapper(this, R.style.popupMenuStyle);
        final PopupMenu menu = new PopupMenu(wrapper, emptyView, Gravity.END);
         menu.inflate(R.menu.sortby_menu);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
               if (menuItem.getItemId() == R.id.createDateTv) {
                    sortArray(4);
                    menu.dismiss();
                    return true;
                } else if (menuItem.getItemId() == R.id.zToATv) {
                    sortArray(3);
                    return true;
                } else if (menuItem.getItemId() == R.id.sizeTv) {
                    sortArray(2);
                    menu.dismiss();
                    return true;
                } else if (menuItem.getItemId() == R.id.aTozTv) {
                    sortArray(1);
                    menu.dismiss();
                    return true;
                }
                return false;
            }
        });
        menu.show();
    }

    public void sortArray(final int sortBy) {

        Collections.sort(fileInfoModelArrayList, new Comparator<FileInfoModel>() {
            @Override
            public int compare(FileInfoModel fileInfoModel, FileInfoModel t1) {
                if (sortBy == 1)
                    return fileInfoModel.getFileName().compareToIgnoreCase(t1.getFileName());//A to Z
                else if (sortBy == 2)
                    return Long.compare(fileInfoModel.getFile().length(), t1.getFile().length());//File size
                else if (sortBy == 3)
                    return t1.getFileName().compareToIgnoreCase(fileInfoModel.getFileName());//Z to A
                else if (sortBy == 4)
                    return Long.compare(fileInfoModel.getFile().lastModified(), t1.getFile().lastModified());//Create Date By
                else if (sortBy == 5)
                    return Long.compare(t1.getFile().lastModified(), fileInfoModel.getFile().lastModified());//Recent updated Date By

                return fileInfoModel.getFileName().compareToIgnoreCase(t1.getFileName());

            }
        });
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == mFileSelectCode && data != null) {
            String filePath = RealPathUtil.getInstance().getRealPath(AllFilesInFolderActivity.this, data.getData());
            File moveFile = new File(filePath);
            boolean isMoved = mDirectoryUtils.moveFile(moveFile.getAbsolutePath(), moveFile.getName(), folderFile.getAbsolutePath() + "/");
            if (isMoved) {
                if (adapter.getRealArray().size() == 0) {
                    String[] names = moveFile.getName().split("\\.");
                    FileInfoModel model = new FileInfoModel(names[0],
                            moveFile.getAbsolutePath().substring(moveFile.getAbsolutePath().lastIndexOf(".")).replace(".", ""),
                            moveFile);
                    fileInfoModelArrayList.add(model);
                    adapter = new AllFilesAdapter(this, fileInfoModelArrayList);
                    allFilesRecycler.setAdapter(adapter);
                    noFileLayout.setVisibility(View.GONE);


                } else {
//                    adapter.refreshArray(moveFile);
                    getFiles();
                }

                StringUtils.getInstance().showSnackbar(AllFilesInFolderActivity.this, "File imported");
            } else {
                StringUtils.getInstance().showSnackbar(AllFilesInFolderActivity.this, "File not imported");
            }
        }
    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pdf_menu, menu);
        menu.findItem(R.id.share).setVisible(false);
        menu.findItem(R.id.moveToFolder).setVisible(false);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }/* else if (item.getItemId() == R.id.premiumImg) {
            startActivity(PremiumScreen.class, null);
            return true;
        }*/
        return false;
    }

    private void getFiles() {
        fileInfoModelArrayList.clear();
        mDirectoryUtils.clearFilterArray();
        ArrayList<File> arrayList = mDirectoryUtils.searchDir(folderFile);
        Log.d("count", arrayList.size() + "");
        if (arrayList.size() > 0) {
            for (File file : arrayList) {
                String[] fileInfo = file.getName().split("\\.");
                if (fileInfo.length == 2)
                    fileInfoModelArrayList.add(new FileInfoModel(fileInfo[0], fileInfo[1], file));
                else {
                    fileInfoModelArrayList.add(new FileInfoModel(fileInfo[0],
                            file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".")).replace(".", ""),
                            file));
                }
            }
            adapter = new AllFilesAdapter(AllFilesInFolderActivity.this, fileInfoModelArrayList);
            allFilesRecycler.setAdapter(adapter);
        } else {
            noFileLayout.setVisibility(View.VISIBLE);
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

}