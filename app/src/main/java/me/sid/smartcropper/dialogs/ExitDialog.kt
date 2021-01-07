package me.sid.smartcropper.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.facebook.ads.*
import kotlinx.android.synthetic.main.exit_dialog.*
import kotlinx.android.synthetic.main.loading_fb_native.*
import me.sid.smartcropper.R
import me.sid.smartcropper.utils.SharePrefData
import java.util.ArrayList

class ExitDialog(internal var _activity: Activity) : Dialog(_activity) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(me.sid.smartcropper.R.layout.exit_dialog)


        exitBtn.setOnClickListener {
            dismiss()
            _activity?.finish()
        }

        noBtn.setOnClickListener {
            dismiss()
        }

        if(!SharePrefData.getInstance().adS_PREFS){
            loadNativeAd()
        }else{
            native_ad_container.visibility=View.GONE
        }

    }

    private var fbnativeAd: NativeAd? = null
    var fbnativeAdLayout: NativeAdLayout? = null
    var fbadView: ConstraintLayout? = null

    private fun loadNativeAd() {
        fbnativeAd = NativeAd(_activity, context.getString(R.string.fb_native))

        val nativeAdListener: NativeAdListener = object : NativeAdListener {
            override fun onMediaDownloaded(ad: Ad) {}

            override fun onError(ad: Ad?, adError: AdError) {
                Log.d("fberror",adError.errorMessage)
                // Native ad failed to load
                native_ad_container.visibility= View.GONE
            }

            override fun onAdLoaded(ad: Ad) {
                Log.d("fberror","loaded")
                native_ad_container.visibility = View.VISIBLE
                //  loadingad.visibility= View.GONE
                if (fbnativeAd == null || fbnativeAd !== ad) {
                    return
                }
                // Inflate Native Ad into Container
                inflateAd(fbnativeAd!!)

            }

            override fun onAdClicked(ad: Ad) {
            }

            override fun onLoggingImpression(ad: Ad) {
            }
        }

        // Request an ad

        // Request an ad
        fbnativeAd?.loadAd(
            fbnativeAd?.buildLoadAdConfig()
                ?.withAdListener(nativeAdListener)
                ?.build()
        )

    }


    private fun inflateAd(nativeAd: NativeAd) {
        nativeAd.unregisterView()
        // Add the Ad view into the ad container.
        fbnativeAdLayout = findViewById(me.sid.smartcropper.R.id.native_ad_container)
        val inflater = LayoutInflater.from(_activity)
        // Inflate the Ad view. The layout referenced should be the one you created in the last step.
        fbadView =
            inflater.inflate(me.sid.smartcropper.R.layout.loading_fb_native, fbnativeAdLayout, false) as ConstraintLayout
        fbnativeAdLayout?.addView(fbadView)
        // Add the AdOptionsView
        val adChoicesContainer = ad_choices_container
        val adOptionsView = AdOptionsView(_activity, nativeAd, fbnativeAdLayout)
        adOptionsView.setIconColor(Color.parseColor("#000000"))
        adChoicesContainer.removeAllViews()
        adChoicesContainer.addView(adOptionsView, 0)
        // Create native UI using the ad metadata.
        val nativeAdIcon = native_ad_icon
        val nativeAdTitle = native_ad_title
        val nativeAdMedia = native_ad_media
        val nativeAdSocialContext = native_ad_social_context
        val nativeAdBody = native_ad_body
        val sponsoredLabel = native_ad_sponsored_label
        val nativeAdCallToAction = native_ad_call_to_action
        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName())
        nativeAdBody.setText(nativeAd.getAdBodyText())
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext())
        nativeAdCallToAction.setVisibility(if (nativeAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE)
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction())
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation())
        // Create a list of clickable views
        val clickableViews = ArrayList<View>()

        clickableViews.add(nativeAdCallToAction)

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
            fbadView,
            nativeAdMedia,
            nativeAdIcon,
            clickableViews
        )

    }


}