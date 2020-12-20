package me.sid.smartcropper.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.AdSettings
import com.facebook.ads.InterstitialAdListener
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import me.sid.smartcropper.App
import me.sid.smartcropper.R
import java.lang.Exception


public class InterstitalAdsInner {
    companion object {
        public var mInterstitialAd: InterstitialAd? = null
        public var splashInterstita: InterstitialAd? = null
        public var interstitialAd_fb: com.facebook.ads.InterstitialAd? = null


        fun loadInterstitialAd(context: Context) {


            if (mInterstitialAd == null) {

                Log.d("mInterstit", "Already loaded")

                mInterstitialAd = InterstitialAd(context)
                mInterstitialAd!!.adUnitId = App.INSTANCE.resources.getString(R.string.admob_interstitial)


            }

            if (!SharePrefData.getInstance().adS_PREFS) {

                if (mInterstitialAd!!.isLoaded) {
                    Log.d("mInterstit", "Already loaded")

                } else {
                    if (!SharePrefData.getInstance().adS_PREFS) {
                        mInterstitialAd!!.loadAd(AdRequest.Builder().build())
                        Log.d("mInterstit", " newReq")
                    }
                }

            }


        }

        /*fun loadSplashInterstitialAd(context: Context) {





            if (splashInterstita == null) {

                Log.d("mInterstit", "Already loaded")
                splashInterstita = InterstitialAd(context)
                splashInterstita!!.adUnitId = context.getString(R.string.live_earth_admob_interstitial_ad)
                //   val adRequest =
            }

            if (!AppUtils.getBillingStatus()) {

                if (splashInterstita!!.isLoaded) {
                    Log.d("mInterstit", "Already loaded")

                } else {
                    if (!AppUtils.getBillingStatus()) {
                        splashInterstita!!.loadAd(AdRequest.Builder().build())
                        Log.d("mInterstit", " newReq")
                    }
                }

            }


        }*/


        fun loadInterstitialAd_fb(context: Context): com.facebook.ads.InterstitialAd {
            AdSettings.addTestDevice("67656de8-d16e-4740-9610-c08ab5e3c503");
            if (interstitialAd_fb == null) {

//                if (BuildConfig.DEBUG)
//                {
//                interstitialAd_fb =
//                    com.facebook.ads.InterstitialAd(context, pref.getString("fb_id","346362029328343_368774563753756"))
//                }
//                else
//                {
                interstitialAd_fb = com.facebook.ads.InterstitialAd(
                    context,
                    context.getString(R.string.fb_interstitial)
                )
//                }

                if (!SharePrefData.getInstance().adS_PREFS) {
                    interstitialAd_fb!!.loadAd()
                }
                Log.d("mInterstit", "== null facebook request")

            } else {
                if (interstitialAd_fb!!.isAdLoaded) {
                    Log.d("mInterstit", "facebook Already loaded")

                } else {
                    if (!SharePrefData.getInstance().adS_PREFS) {
                        interstitialAd_fb!!.loadAd()
                    }


                    Log.d("mInterstit", "facebook request")

                }
            }

//            interstitialAd_fb!!.setAdListener(object : InterstitialAdListener {
//                override fun onInterstitialDisplayed(ad: Ad) {
//                    Log.d("FacebookAdsLog", "Displayed")
//                }
//
//                override fun onInterstitialDismissed(ad: Ad) {
//                    Log.d("FacebookAdsLog", "Dismissed")
////                    if (pref.getBoolean("ads", true)) {
////                        interstitialAd_fb!!.loadAd()
////                    }
//                }
//
//                override fun onError(ad: Ad, adError: AdError) {
//                    Log.d("FacebookAdsLog", "Errors")
//                    //  interstitialAd_fb!!.loadAd()
//                }
//
//                override fun onAdLoaded(ad: Ad) {
//                    Log.d("FacebookAdsLog", "Loaded")
//                }
//
//                override fun onAdClicked(ad: Ad) {
//                    Log.d("FacebookAdsLog", "Clicked")
//
//
//                }
//
//                override fun onLoggingImpression(ad: Ad) {
//                    Log.d("FacebookAdsLog", "Impression")
//
//
//                }
//            })
            return interstitialAd_fb as com.facebook.ads.InterstitialAd
        }

    }

/*    fun startActivityMadiationAdmobPriority(context: Activity, newIntent: Intent) {

        if (interstitialAd_fb != null) {
            if (interstitialAd_fb!!.isAdLoaded && !interstitialAd_fb!!.isAdInvalidated) {
                interstitialAd_fb!!.show()

                var interstitialAdListener: InterstitialAdListener =
                    object : InterstitialAdListener {
                        override fun onInterstitialDisplayed(ad: Ad) {
                            // Interstitial ad displayed callback
                            Log.e("FacebookAdsLog1", "Interstitial ad displayed.")
                        }

                        override fun onInterstitialDismissed(ad: Ad) {
                            //                if (pref.getBoolean("ads", true)) {
                            //                    interstitialAd_fb!!.loadAd()
                            //                }
                            context.startActivity(newIntent)

                            interstitialAd_fb = null
                            //                context.finish()

                            // Interstitial dismissed callback
                            Log.e("FacebookAdsLog1", "Interstitial ad dismissed.")
                        }

                        override fun onError(ad: Ad, adError: AdError) {
                            // Ad error callback
                            Log.e(
                                "FacebookAdsLog1",
                                "Interstitial ad failed to load: " + adError.getErrorMessage()
                            )
                        }

                        override fun onAdLoaded(ad: Ad) {
                            // Interstitial ad is loaded and ready to be displayed
                            Log.d(
                                "FacebookAdsLog1",
                                "Interstitial ad is loaded and ready to be displayed!"
                            )
                            // Show the ad

                        }

                        override fun onAdClicked(ad: Ad) {
                            // Ad clicked callback
                            Log.d("FacebookAdsLog1", "Interstitial ad clicked!")
                        }

                        override fun onLoggingImpression(ad: Ad) {
                            // Ad impression logged callback
                            Log.d("FacebookAdsLog1", "Interstitial ad impression logged!")
                        }
                    }

                interstitialAd_fb?.buildLoadAdConfig()?.withAdListener(interstitialAdListener)


            } else {

                if (mInterstitialAd!!.isLoaded) {
                    mInterstitialAd!!.show()
                    mInterstitialAd!!.adListener = object : AdListener() {
                        override fun onAdClosed() {
                            context.startActivity(newIntent)

                            *//*if (pref.getBoolean("ads", true)) {
                                interstitialAd_fb!!.loadAd()
                            }*//*
                            //                    context.finish()

                        }
                    }
                } else {
                    context.startActivity(newIntent)
                    //                context.finish()
                }

            }
        } else {
            context.startActivity(newIntent)
        }
    }*/


    /*fun startActivityMadiationAdmobPriority(context: Activity, newIntent: Intent) {

        if (mInterstitialAd != null && mInterstitialAd!!.isLoaded && !SharePrefData.getInstance().adS_PREFS) {
            mInterstitialAd!!.show()

            mInterstitialAd?.adListener = object : AdListener() {
                override fun onAdClosed() {

                    *//*if (!SharePrefData.getInstance().adS_PREFS) {
                        mInterstitialAd!!.loadAd(AdRequest.Builder().build())
                    }*//*

                    try {
                        context.startActivity(newIntent)
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
//                    context.finish()

                }
            }

        } else {

            if (!SharePrefData.getInstance().adS_PREFS && interstitialAd_fb != null && interstitialAd_fb!!.isAdLoaded && !interstitialAd_fb!!.isAdInvalidated) {
                interstitialAd_fb!!.show()

                var interstitialAdListener: InterstitialAdListener =
                    object : InterstitialAdListener {
                        override fun onInterstitialDisplayed(ad: Ad) {
                            // Interstitial ad displayed callback
                            Log.e("FacebookAdsLog1", "Interstitial ad displayed.")
                        }

                        override fun onInterstitialDismissed(ad: Ad) {

//                if (pref.getBoolean("ads", true)) {
//                    interstitialAd_fb!!.loadAd()

//                interstitialAd_fb = null
//                }
                            if (!SharePrefData.getInstance().adS_PREFS) {
                                interstitialAd_fb!!.loadAd()
                            }
                            try {
                                context.startActivity(newIntent)
                            } catch (e: ActivityNotFoundException) {
                                e.printStackTrace()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
//                context.finish()


                            // Interstitial dismissed callback
                            Log.e("FacebookAdsLog1", "Interstitial ad dismissed.")
                        }

                        override fun onError(ad: Ad, adError: AdError) {
                            // Ad error callback
                            Log.e(
                                "FacebookAdsLog1",
                                "Interstitial ad failed to load: " + adError.getErrorMessage()
                            )
                        }

                        override fun onAdLoaded(ad: Ad) {
                            // Interstitial ad is loaded and ready to be displayed
                            Log.d(
                                "FacebookAdsLog1",
                                "Interstitial ad is loaded and ready to be displayed!"
                            )
                            // Show the ad

                        }

                        override fun onAdClicked(ad: Ad) {
                            // Ad clicked callback
                            Log.d("FacebookAdsLog1", "Interstitial ad clicked!")
                        }

                        override fun onLoggingImpression(ad: Ad) {
                            // Ad impression logged callback
                            Log.d("FacebookAdsLog1", "Interstitial ad impression logged!")
                        }
                    }

                interstitialAd_fb?.buildLoadAdConfig()?.withAdListener(interstitialAdListener)


            } else {
                try {
                    context.startActivity(newIntent)
//                context.finish()
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }



    fun showFb(context: Activity) {
        if (!SharePrefData.getInstance().adS_PREFS && interstitialAd_fb != null && interstitialAd_fb!!.isAdLoaded && !interstitialAd_fb!!.isAdInvalidated) {
            interstitialAd_fb!!.show()

            var interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
                override fun onInterstitialDisplayed(ad: Ad) {
                    // Interstitial ad displayed callback
                    Log.e("FacebookAdsLog1", "Interstitial ad displayed.")
                }

                override fun onInterstitialDismissed(ad: Ad) {

//                if (pref.getBoolean("ads", true)) {
//                    interstitialAd_fb!!.loadAd()

//                interstitialAd_fb = null
//                }
                    if (!SharePrefData.getInstance().adS_PREFS) {
                        interstitialAd_fb!!.loadAd()
                    }
                    // Interstitial dismissed callback
                    Log.e("FacebookAdsLog1", "Interstitial ad dismissed.")
                }

                override fun onError(ad: Ad, adError: AdError) {
                    // Ad error callback
                    Log.e(
                        "FacebookAdsLog1",
                        "Interstitial ad failed to load: " + adError.getErrorMessage()
                    )
                }

                override fun onAdLoaded(ad: Ad) {
                    // Interstitial ad is loaded and ready to be displayed
                    Log.d("FacebookAdsLog1", "Interstitial ad is loaded and ready to be displayed!")
                    // Show the ad

                }

                override fun onAdClicked(ad: Ad) {
                    // Ad clicked callback
                    Log.d("FacebookAdsLog1", "Interstitial ad clicked!")
                }

                override fun onLoggingImpression(ad: Ad) {
                    // Ad impression logged callback
                    Log.d("FacebookAdsLog1", "Interstitial ad impression logged!")
                }
            }

            interstitialAd_fb?.buildLoadAdConfig()?.withAdListener(interstitialAdListener)


        }


    }

    fun finishActivityMadiationClose(context: Activity) {
        if (!SharePrefData.getInstance().adS_PREFS && interstitialAd_fb != null && interstitialAd_fb!!.isAdLoaded && !interstitialAd_fb!!.isAdInvalidated) {
            interstitialAd_fb!!.show()

            var interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
                override fun onInterstitialDisplayed(ad: Ad) {
                    // Interstitial ad displayed callback
                    Log.e("FacebookAdsLog1", "Interstitial ad displayed.")
                }

                override fun onInterstitialDismissed(ad: Ad) {

//                if (pref.getBoolean("ads", true)) {
//                    interstitialAd_fb!!.loadAd()

//                interstitialAd_fb = null
//                }
                    if (!SharePrefData.getInstance().adS_PREFS) {
                        interstitialAd_fb!!.loadAd()
                    }
                    //    context.startActivity(newIntent)
                    context.finish()

                    // Interstitial dismissed callback
                    Log.e("FacebookAdsLog1", "Interstitial ad dismissed.")
                }

                override fun onError(ad: Ad, adError: AdError) {
                    // Ad error callback
                    Log.e(
                        "FacebookAdsLog1",
                        "Interstitial ad failed to load: " + adError.getErrorMessage()
                    )
                }

                override fun onAdLoaded(ad: Ad) {
                    // Interstitial ad is loaded and ready to be displayed
                    Log.d("FacebookAdsLog1", "Interstitial ad is loaded and ready to be displayed!")
                    // Show the ad

                }

                override fun onAdClicked(ad: Ad) {
                    // Ad clicked callback
                    Log.d("FacebookAdsLog1", "Interstitial ad clicked!")
                }

                override fun onLoggingImpression(ad: Ad) {
                    // Ad impression logged callback
                    Log.d("FacebookAdsLog1", "Interstitial ad impression logged!")
                }
            }

            interstitialAd_fb?.buildLoadAdConfig()?.withAdListener(interstitialAdListener)


        } else {

            if (mInterstitialAd != null && mInterstitialAd!!.isLoaded && !SharePrefData.getInstance().adS_PREFS) {
                mInterstitialAd!!.show()
                mInterstitialAd!!.adListener = object : AdListener() {
                    override fun onAdClosed() {

                        if (!SharePrefData.getInstance().adS_PREFS) {
                            mInterstitialAd!!.loadAd(AdRequest.Builder().build())
                        }

                        // context.startActivity(newIntent)
                        context.finish()

                    }
                }
            } else {
                //context.startActivity(newIntent)
                context.finish()
            }

        }


    }


    fun finishActivityFbClose(context: Activity) {
        if (!SharePrefData.getInstance().adS_PREFS && interstitialAd_fb != null && interstitialAd_fb!!.isAdLoaded && !interstitialAd_fb!!.isAdInvalidated) {
            interstitialAd_fb!!.show()


            var interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
                override fun onInterstitialDisplayed(ad: Ad) {
                    // Interstitial ad displayed callback
                    Log.e("FacebookAdsLog1", "Interstitial ad displayed.")
                }

                override fun onInterstitialDismissed(ad: Ad) {

//                if (pref.getBoolean("ads", true)) {
//                    interstitialAd_fb!!.loadAd()

//                interstitialAd_fb = null
//                }
                    if (!SharePrefData.getInstance().adS_PREFS) {
                        interstitialAd_fb!!.loadAd()
                    }
                    //    context.startActivity(newIntent)
                    context.finish()
                    // Interstitial dismissed callback
                    Log.e("FacebookAdsLog1", "Interstitial ad dismissed.")
                }

                override fun onError(ad: Ad, adError: AdError) {
                    // Ad error callback
                    Log.e(
                        "FacebookAdsLog1",
                        "Interstitial ad failed to load: " + adError.getErrorMessage()
                    )
                }

                override fun onAdLoaded(ad: Ad) {
                    // Interstitial ad is loaded and ready to be displayed
                    Log.d("FacebookAdsLog1", "Interstitial ad is loaded and ready to be displayed!")
                    // Show the ad

                }

                override fun onAdClicked(ad: Ad) {
                    // Ad clicked callback
                    Log.d("FacebookAdsLog1", "Interstitial ad clicked!")
                }

                override fun onLoggingImpression(ad: Ad) {
                    // Ad impression logged callback
                    Log.d("FacebookAdsLog1", "Interstitial ad impression logged!")
                }
            }

            interstitialAd_fb?.buildLoadAdConfig()?.withAdListener(interstitialAdListener)


        } else {

            context.finish()

        }


    }

*/
    fun ShowAdMob(context: Activity) {
        if (mInterstitialAd != null && mInterstitialAd!!.isLoaded && !SharePrefData.getInstance().adS_PREFS) {
            mInterstitialAd!!.show()
            mInterstitialAd!!.adListener = object : AdListener() {
                override fun onAdClosed() {
                    if (!SharePrefData.getInstance().adS_PREFS) {
                        mInterstitialAd!!.loadAd(AdRequest.Builder().build())
                    }
//                    context.startActivity(newIntent)
                context.finish()

                }
            }
        } else {
//            context.startActivity(newIntent)
            context.finish()
        }

    }


    fun ShowAdMobNoFinish(context: Activity) {
        if (mInterstitialAd != null && mInterstitialAd!!.isLoaded && !SharePrefData.getInstance().adS_PREFS) {
            mInterstitialAd!!.show()
            mInterstitialAd!!.adListener = object : AdListener() {
                override fun onAdClosed() {
                    if (!SharePrefData.getInstance().adS_PREFS) {
                        mInterstitialAd!!.loadAd(AdRequest.Builder().build())
                    }
//                    context.startActivity(newIntent)
                 //   context.finish()

                }
            }
        } else {
//            context.startActivity(newIntent)
       //     context.finish()
        }

    }


    fun showFbClose(context: Activity) {
        if (!SharePrefData.getInstance().adS_PREFS && interstitialAd_fb != null && interstitialAd_fb!!.isAdLoaded && !interstitialAd_fb!!.isAdInvalidated) {
            interstitialAd_fb!!.show()

            var interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
                override fun onInterstitialDisplayed(ad: Ad) {
                    // Interstitial ad displayed callback
                    Log.e("FacebookAdsLog1", "Interstitial ad displayed.")
                }

                override fun onInterstitialDismissed(ad: Ad) {

//                if (pref.getBoolean("ads", true)) {
//                    interstitialAd_fb!!.loadAd()

//                interstitialAd_fb = null
//                }
                    if (!SharePrefData.getInstance().adS_PREFS) {
                        interstitialAd_fb!!.loadAd()
                    }
//                    try {
//                        context.startActivity(newIntent)
//                    } catch (e: ActivityNotFoundException) {
//                        e.printStackTrace()
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
                context.finish()


                    // Interstitial dismissed callback
                    Log.e("FacebookAdsLog1", "Interstitial ad dismissed.")
                }

                override fun onError(ad: Ad, adError: AdError) {
                    // Ad error callback
                    Log.e(
                            "FacebookAdsLog1",
                            "Interstitial ad failed to load: " + adError.getErrorMessage()
                    )
                }

                override fun onAdLoaded(ad: Ad) {
                    // Interstitial ad is loaded and ready to be displayed
                    Log.d("FacebookAdsLog1", "Interstitial ad is loaded and ready to be displayed!")
                    // Show the ad

                }

                override fun onAdClicked(ad: Ad) {
                    // Ad clicked callback
                    Log.d("FacebookAdsLog1", "Interstitial ad clicked!")
                }

                override fun onLoggingImpression(ad: Ad) {
                    // Ad impression logged callback
                    Log.d("FacebookAdsLog1", "Interstitial ad impression logged!")
                }
            }

            interstitialAd_fb?.buildLoadAdConfig()?.withAdListener(interstitialAdListener)


        } else {
            context.finish()

        }


    }

    fun showFb(context: Activity) {
        if (!SharePrefData.getInstance().adS_PREFS && interstitialAd_fb != null && interstitialAd_fb!!.isAdLoaded && !interstitialAd_fb!!.isAdInvalidated) {
            interstitialAd_fb!!.show()

            var interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
                override fun onInterstitialDisplayed(ad: Ad) {
                    // Interstitial ad displayed callback
                    Log.e("FacebookAdsLog1", "Interstitial ad displayed.")
                }

                override fun onInterstitialDismissed(ad: Ad) {

//                if (pref.getBoolean("ads", true)) {
//                    interstitialAd_fb!!.loadAd()

//                interstitialAd_fb = null
//                }
                    if (!SharePrefData.getInstance().adS_PREFS) {
                        interstitialAd_fb!!.loadAd()
                    }
//                    try {
//                        context.startActivity(newIntent)
//                    } catch (e: ActivityNotFoundException) {
//                        e.printStackTrace()
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                    context.finish()


                    // Interstitial dismissed callback
                    Log.e("FacebookAdsLog1", "Interstitial ad dismissed.")
                }

                override fun onError(ad: Ad, adError: AdError) {
                    // Ad error callback
                    Log.e(
                            "FacebookAdsLog1",
                            "Interstitial ad failed to load: " + adError.getErrorMessage()
                    )
                }

                override fun onAdLoaded(ad: Ad) {
                    // Interstitial ad is loaded and ready to be displayed
                    Log.d("FacebookAdsLog1", "Interstitial ad is loaded and ready to be displayed!")
                    // Show the ad

                }

                override fun onAdClicked(ad: Ad) {
                    // Ad clicked callback
                    Log.d("FacebookAdsLog1", "Interstitial ad clicked!")
                }

                override fun onLoggingImpression(ad: Ad) {
                    // Ad impression logged callback
                    Log.d("FacebookAdsLog1", "Interstitial ad impression logged!")
                }
            }

            interstitialAd_fb?.buildLoadAdConfig()?.withAdListener(interstitialAdListener)


        } else {
//            context.finish()

        }


    }

    /* fun ShowSplashAdMob(context: Activity, newIntent: Intent) {
         if (splashInterstita!!.isLoaded && !AppUtils.getBillingStatus()) {
             splashInterstita!!.show()
         } else {
             context.startActivity(newIntent)
 //            context.finish()
         }
         splashInterstita!!.adListener = object : AdListener() {
             override fun onAdClosed() {
                 if (!AppUtils.getBillingStatus()) {
                     splashInterstita!!.loadAd(AdRequest.Builder().build())
                 }
                 context.startActivity(newIntent)
 //                context.finish()

             }
         }
     }*/


    fun adMobShoeClose(context: Activity, newIntent: Intent) {
        if (mInterstitialAd != null && mInterstitialAd!!.isLoaded && !SharePrefData.getInstance().adS_PREFS) {
            mInterstitialAd!!.show()
            mInterstitialAd!!.adListener = object : AdListener() {
                override fun onAdClosed() {
                    if (!SharePrefData.getInstance().adS_PREFS) {
                        mInterstitialAd!!.loadAd(AdRequest.Builder().build())
                    }
                    context.startActivity(newIntent)
                    context.finish()

                }
            }
        } else {
            context.startActivity(newIntent)
            context.finish()
        }

    }


    fun adMobShow(context: Activity, newIntent: Intent) {
        if (mInterstitialAd != null && mInterstitialAd!!.isLoaded && !SharePrefData.getInstance().adS_PREFS) {
            mInterstitialAd!!.show()
            mInterstitialAd!!.adListener = object : AdListener() {
                override fun onAdClosed() {
                    if (!SharePrefData.getInstance().adS_PREFS) {
                        mInterstitialAd!!.loadAd(AdRequest.Builder().build())
                    }
                    context.startActivity(newIntent)


                }
            }
        } else {
            context.startActivity(newIntent)
        }

    }


    fun adMobStartActivityResult(context: Activity, newIntent: Intent, Code: Int) {
        if (mInterstitialAd!!.isLoaded && !SharePrefData.getInstance().adS_PREFS) {
            mInterstitialAd!!.show()
        } else {
            context.startActivityForResult(newIntent, Code)
//            context.finish()
        }
        mInterstitialAd!!.adListener = object : AdListener() {
            override fun onAdClosed() {
                if (!SharePrefData.getInstance().adS_PREFS) {
                    mInterstitialAd!!.loadAd(AdRequest.Builder().build())
                }
                context.startActivityForResult(newIntent, Code)
//                context.finish()

            }
        }
    }

    fun adMobShowCloseOnly(context: Activity) {


        if (mInterstitialAd!!.isLoaded && !SharePrefData.getInstance().adS_PREFS) {
            mInterstitialAd!!.show()
        } else {

            context.finish()
        }
        mInterstitialAd!!.adListener = object : AdListener() {
            override fun onAdClosed() {
                if (!SharePrefData.getInstance().adS_PREFS) {
                    mInterstitialAd!!.loadAd(AdRequest.Builder().build())
                }

                context.finish()

            }
        }
    }


    /*fun specificAdForNearByActivity(context: Context, name:String) {
        if (pref.getBoolean("ads", true) && interstitialAd_fb!=null && interstitialAd_fb!!.isAdLoaded && !interstitialAd_fb!!.isAdInvalidated ) {
            interstitialAd_fb!!.show()
        } else {

            if (mInterstitialAd!!.isLoaded && pref.getBoolean("ads", true)) {
                mInterstitialAd!!.show()
            } else {
                try {
                    MapUtils.openNearBy(context, name)

                }catch (e:ActivityNotFoundException){
                    e.printStackTrace()
                }catch (e:Exception){
                    e.printStackTrace()
                }

//                context.finish()
            }
            mInterstitialAd!!.adListener = object : AdListener() {
                override fun onAdClosed() {

                    if (pref.getBoolean("ads", true)) {
                        mInterstitialAd!!.loadAd(AdRequest.Builder().build())
                    }

                    try {
                        MapUtils.openNearBy(context, name)
                    }catch (e:ActivityNotFoundException){
                        e.printStackTrace()
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
//                    context.finish()

                }
            }
        }


        interstitialAd_fb!!.setAdListener(object : InterstitialAdListener {
            override fun onInterstitialDisplayed(ad: Ad) {
                Log.d("FacebookAdsLog1", pref.getInt("counter", 0).toString() + "onInterstitialDisplayed")


//                interstitialAd_fb!!.loadAd()


//                if (pref.getInt("counter", 0) > 0 && pref.getInt("counter", 0) < 12) {
//                    editor.putInt("counter", pref.getInt("counter", 0) + 1).commit()

//                    context.startActivity(Intent(context, Transparent::class.java).putExtra("activity", 1))


//                    interstitialAd_fb=null

//                    interstitialAd_fb!!.set
//                } else {
//
//                    editor.putInt("counter", 0).commit()
//                }


//                interstitialAd_fb = null


//                if (pref.getBoolean("ads", true)) {
//                    interstitialAd_fb!!.loadAd()
//                }


            }

            override fun onInterstitialDismissed(ad: Ad) {

//                if (pref.getBoolean("ads", true)) {
//                    interstitialAd_fb!!.loadAd()

//                interstitialAd_fb = null
//                }
                if (pref.getBoolean("ads", true)) {
                    interstitialAd_fb!!.loadAd()
                }
                try {
                    MapUtils.openNearBy(context, name)
                }catch (e:ActivityNotFoundException){
                    e.printStackTrace()
                }catch (e:Exception){
                    e.printStackTrace()
                }
//                context.finish()


            }

            override fun onError(ad: Ad, adError: AdError) {
                Log.d("FacebookAdsLog1", "onError")
//                interstitialAd_fb = null



            }

            override fun onAdLoaded(ad: Ad) {
                Log.d("FacebookAdsLog1", "onAdLoaded")

            }

            override fun onAdClicked(ad: Ad) {
                Log.d("FacebookAdsLog1", "onAdClicked")

//                editor.putInt("counter", 1).commit()
            }

            override fun onLoggingImpression(ad: Ad) {
                Log.d("FacebookAdsLog1", "onLoggingImpression")


//                (interstitialAd_fb as com.facebook.ads.InterstitialAd).destroy()


            }
        })
    }*/


    fun adMobShowOnly(context: Activity) {
        if (mInterstitialAd != null && mInterstitialAd!!.isLoaded && !SharePrefData.getInstance().adS_PREFS) {
            if (!SharePrefData.getInstance().adS_PREFS) {
                mInterstitialAd!!.show()
                mInterstitialAd!!.adListener = object : AdListener() {
                    override fun onAdClosed() {
                        if (!SharePrefData.getInstance().adS_PREFS) {
                            mInterstitialAd!!.loadAd(AdRequest.Builder().build())
                        }

                    }
                }
            }


        }
    }

    /* fun Backpress(context: Activity) {

 //        if (interstitialAd_fb!!.isAdLoaded && !interstitialAd_fb!!.isAdInvalidated) {
 //            interstitialAd_fb!!.show()
 //        } else {

         if (interstitialAd_fb!!.isAdLoaded) {
             interstitialAd_fb!!.show()
         } else {
             CustomDialogue(context).show()

         }

         interstitialAd_fb!!.setAdListener(object : InterstitialAdListener {
             override fun onInterstitialDisplayed(ad: Ad) {
 //                Log.d("FacebookAdsLog1", pref.getInt("counter", 0).toString() + "onInterstitialDisplayed")


 //                interstitialAd_fb!!.loadAd()


 //                if (pref.getInt("counter", 0) > 0 && pref.getInt("counter", 0) < 12) {
 //                    editor.putInt("counter", pref.getInt("counter", 0) + 1).commit()

 //                    context.startActivity(Intent(context, Transparent::class.java).putExtra("activity", 1))


 //                    interstitialAd_fb=null

 //                    interstitialAd_fb!!.set
 //                } else {
 //
 //                    editor.putInt("counter", 0).commit()
 //                }


 //                interstitialAd_fb = null


 //                if (pref.getBoolean("ads", true)) {
 //                    interstitialAd_fb!!.loadAd()
 //                }


             }

             override fun onInterstitialDismissed(ad: Ad) {

 //                if (pref.getBoolean("ads", true)) {
 //                    interstitialAd_fb!!.loadAd()

 //                interstitialAd_fb = null
 //                }
                 if (pref.getBoolean("ads", true)) {
                     interstitialAd_fb!!.loadAd()
                 }
                 CustomDialogue(context).show()
 //                context.finish()


             }

             override fun onError(ad: Ad, adError: AdError) {
                 Log.d("FacebookAdsLog1", "onError")
 //                interstitialAd_fb = null



             }

             override fun onAdLoaded(ad: Ad) {
                 Log.d("FacebookAdsLog1", "onAdLoaded")

             }

             override fun onAdClicked(ad: Ad) {
                 Log.d("FacebookAdsLog1", "onAdClicked")

 //                editor.putInt("counter", 1).commit()
             }

             override fun onLoggingImpression(ad: Ad) {
                 Log.d("FacebookAdsLog1", "onLoggingImpression")


 //                (interstitialAd_fb as com.facebook.ads.InterstitialAd).destroy()


             }
         })

 //        interstitialAd_fb!!.adListener = object : AdListener() {
 //            override fun onAdClosed() {
 //                CustomDialogue(context).show()
 //
 //
 //            }
 //        }
 //        }

 //
 //        interstitialAd_fb!!.setAdListener(object : InterstitialAdListener {
 //            override fun onInterstitialDisplayed(ad: Ad) {
 //                Log.d("FacebookAdsLog", "onInterstitialDisplayed")
 //            }
 //
 //            override fun onInterstitialDismissed(ad: Ad) {
 //                CustomDialogue(context).show()
 //
 //            }
 //
 //            override fun onError(ad: Ad, adError: AdError) {
 //                Log.d("FacebookAdsLog", "onError")
 //
 //            }
 //
 //            override fun onAdLoaded(ad: Ad) {
 //                Log.d("FacebookAdsLog", "onAdLoaded")
 //
 //            }
 //
 //            override fun onAdClicked(ad: Ad) {
 //                Log.d("FacebookAdsLog", "onAdClicked")
 //            }
 //
 //            override fun onLoggingImpression(ad: Ad) {
 //                Log.d("FacebookAdsLog", "onLoggingImpression")
 //
 //
 //            }
 //        })
     }
 */


    fun startIntentChooserMediation(context: Activity, newIntent: Intent) {

        if (mInterstitialAd != null && mInterstitialAd!!.isLoaded && !SharePrefData.getInstance().adS_PREFS) {
            mInterstitialAd!!.show()

            mInterstitialAd?.adListener = object : AdListener() {
                override fun onAdClosed() {

                    if (!SharePrefData.getInstance().adS_PREFS) {
                        mInterstitialAd!!.loadAd(AdRequest.Builder().build())
                    }

                    try {
                        context.startActivity(Intent.createChooser(newIntent, "Share Via"))
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
//                    context.finish()

                }
            }

        } else {
            try {
                context.startActivity(Intent.createChooser(newIntent, "Share Via"))
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

//                context.finish()
        }


    }

}
