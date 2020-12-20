package me.sid.smartcropper.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import me.sid.smartcropper.R


@SuppressLint("StaticFieldLeak")
class BannerAds {

    companion object {
        lateinit var prefs: SharedPreferences
        lateinit var editor: SharedPreferences.Editor

        private var adView: AdView? = null
        var adContainer: RelativeLayout? = null
        var adviewfb: com.facebook.ads.AdView? = null
        lateinit var sizesfb: com.facebook.ads.AdSize
        lateinit var goolglesize: AdSize


        fun loadFB(adslayout: RelativeLayout, context: Context, size: String) {



            if (size.equals("small")) {
                sizesfb = com.facebook.ads.AdSize.BANNER_HEIGHT_50
            } else if (size.equals("rectangular")) {
                sizesfb = com.facebook.ads.AdSize.RECTANGLE_HEIGHT_250
            } else if (size.equals("large")) {
                sizesfb = com.facebook.ads.AdSize.BANNER_HEIGHT_90
            }

            adContainer = adslayout

            if (size.equals("small") || size.equals("large")) {
                adviewfb = com.facebook.ads.AdView(
                    context,
                    context.getString(R.string.fb_banner),

                    sizesfb
                )
            } else {
                adviewfb = com.facebook.ads.AdView(
                    context,
                        context.getString(R.string.fb_rectangle),

                    sizesfb
                )
            }

            adContainer!!.addView(adviewfb)

            val adListener: AdListener = object : AdListener {
                override fun onError(ad: Ad, adError: AdError) {
                    // Ad error callback
                    adContainer!!.removeAllViews()

                    if (!SharePrefData.getInstance().adS_PREFS) {

                        loadAdmob(
                            context,
                            size,
                            adslayout
                        )
                    } else {
                        adslayout.visibility = View.GONE
                    }
                }

                override fun onAdLoaded(ad: Ad) {
                    // Ad loaded callback
                }

                override fun onAdClicked(ad: Ad) {
                    // Ad clicked callback
                }

                override fun onLoggingImpression(ad: Ad) {
                    // Ad impression logged callback
                }
            }


//        if (appPurchasesPrefs.getItemDetail().equals("") && appPurchasesPrefs.getProductId().equals("")) {

            if (!SharePrefData.getInstance().adS_PREFS) {

                adviewfb!!.loadAd(adviewfb?.buildLoadAdConfig()?.withAdListener(adListener)?.build())
            } else {
                adslayout.visibility = View.GONE
            }


//        }
        }

        fun loadAdmob(context: Context, size: String, layout: RelativeLayout): AdView {



            fun destroy() {
                adView!!.destroy()
            }

            adContainer = layout
            adView = AdView(context)


            if (size.equals("small")) {

                goolglesize = AdSize.BANNER
                adView!!.adUnitId = context.getString(R.string.admob_banner)
            } else if (size.equals("large")) {

                goolglesize = AdSize.LARGE_BANNER
                adView!!.adUnitId = context.getString(R.string.admob_banner)
            } else if (size.equals("rectangular")) {

                goolglesize = AdSize.MEDIUM_RECTANGLE
                adView!!.adUnitId =context.getString(R.string.admob_banner)
            }

            adView!!.adSize =
                goolglesize


            if (!SharePrefData.getInstance().adS_PREFS) {

                adView!!.loadAd(AdRequest.Builder().build())
            } else {
                layout.visibility = View.GONE
            }
//        }

            adView!!.adListener = object : com.google.android.gms.ads.AdListener() {

                override fun onAdLoaded() {
                    super.onAdLoaded()

                }
            }

            var params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT

            )
            adContainer!!.addView(adView, params)

            return adView as AdView
        }


        fun loadAdmobMed(context: Context, size: String, layout: RelativeLayout): AdView {


            fun destroy() {
                adView!!.destroy()
            }

            adContainer = layout
            adView = AdView(context)


            if (size.equals("small")) {

                goolglesize = AdSize.BANNER
                adView!!.adUnitId = context.getString(R.string.admob_banner)
            } else if (size.equals("large")) {

                goolglesize = AdSize.LARGE_BANNER
                adView!!.adUnitId =context.getString(R.string.admob_banner)
            } else if (size.equals("rectangular")) {

                goolglesize = AdSize.MEDIUM_RECTANGLE
                adView!!.adUnitId =context.getString(R.string.admob_banner)
            }

            adView!!.adSize =
                    goolglesize


            if (!SharePrefData.getInstance().adS_PREFS) {

                adView!!.loadAd(AdRequest.Builder().build())
            } else {
                layout.visibility = View.GONE
            }
//        }

            adView!!.adListener = object : com.google.android.gms.ads.AdListener() {

                override fun onAdLoaded() {
                    super.onAdLoaded()

                }

                override fun onAdFailedToLoad(p0: Int) {
                    super.onAdFailedToLoad(p0)

                    layout.removeAllViews()
                    if (!SharePrefData.getInstance().adS_PREFS) {

                        loadFBwithoutMed(layout, context, size)
                    } else {
                        layout.visibility = View.GONE
                    }

                }
            }

            var params = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT

            )
            adContainer!!.addView(adView, params)

            return adView as AdView
        }


        fun loadFBwithoutMed(adslayout: RelativeLayout, context: Context, size: String) {

            if (size.equals("small")) {
                sizesfb = com.facebook.ads.AdSize.BANNER_HEIGHT_50
            } else if (size.equals("rectangular")) {
                sizesfb = com.facebook.ads.AdSize.RECTANGLE_HEIGHT_250
            } else if (size.equals("large")) {
                sizesfb = com.facebook.ads.AdSize.BANNER_HEIGHT_90
            }

            adContainer = adslayout

            if (size.equals("small") || size.equals("large")) {
                adviewfb = com.facebook.ads.AdView(
                        context,
                    context.getString(R.string.fb_banner),

                        sizesfb
                )
            } else {
                adviewfb = com.facebook.ads.AdView(
                        context,
                    context.getString(R.string.fb_rectangle),

                        sizesfb
                )
            }

            adContainer!!.addView(adviewfb)


            if (!SharePrefData.getInstance().adS_PREFS) {

                adviewfb!!.loadAd()
            } else {
                adslayout.visibility = View.GONE
            }


        }


    }




}


