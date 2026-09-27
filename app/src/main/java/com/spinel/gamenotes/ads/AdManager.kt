package com.spinel.gamenotes.ads

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.spinel.gamenotes.BuildConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.util.Date

/**
 * Manages Google AdMob Test Ads according to user requirements and policies:
 * 1. App Open Ad: Only displays on Warm Start (returning from background), never on Cold Start.
 * 2. Interstitial Ads:
 *    - Note action counter: Triggers on 3 clicks/edits on notes, then resets.
 *    - Sub-screen visit counter: Triggers on 5 visits to Trash, AI Chat History, or Settings, then resets.
 * 3. Strictly isolated from Floating Overlay and Keyboard areas.
 * 4. Respects BuildConfig.ENABLE_ADS.
 */
object AdManager : Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private const val TAG = "AdManager"

    // Production Google AdMob Ad Unit IDs
    const val TEST_BANNER_AD_ID = "ca-app-pub-9118481973136364/9509397697"
    const val TEST_INTERSTITIAL_AD_ID = "ca-app-pub-9118481973136364/9852178475"
    const val TEST_APP_OPEN_AD_ID = "ca-app-pub-9118481973136364/4720105444"

    private var currentActivity: Activity? = null
    private var isColdStart = true
    private var isAppInForeground = false

    // App Open Ad State
    private var appOpenAd: AppOpenAd? = null
    private var isAppOpenAdLoading = false
    private var isShowingAppOpenAd = false
    private var appOpenAdLoadTime: Long = 0

    // Interstitial Ad State
    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoading = false
    private var isShowingInterstitial = false

    // Counters for Interstitial Ads
    private var noteActionCount = 0
    private var subScreenVisitCount = 0

    fun init(application: Application) {
        if (!BuildConfig.ENABLE_ADS) {
            Log.d(TAG, "Ads are disabled via BuildConfig (Personal Flavor).")
            return
        }

        try {
            MobileAds.initialize(application) { initializationStatus ->
                Log.d(TAG, "MobileAds initialized: $initializationStatus")
                try {
                    loadAppOpenAd(application)
                    loadInterstitialAd(application)
                } catch (t: Throwable) {
                    Log.e(TAG, "Error loading initial ads", t)
                }
            }

            application.registerActivityLifecycleCallbacks(this)
            try {
                ProcessLifecycleOwner.get().lifecycle.addObserver(this)
            } catch (t: Throwable) {
                Log.e(TAG, "ProcessLifecycleOwner error", t)
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Error initializing MobileAds", e)
        }
    }

    // =========================================================================
    // APP OPEN AD (WARM START ONLY)
    // =========================================================================

    private fun loadAppOpenAd(context: Context) {
        if (!BuildConfig.ENABLE_ADS || isAppOpenAdLoading || isAppOpenAdAvailable()) {
            return
        }

        try {
            isAppOpenAdLoading = true
            val request = AdRequest.Builder().build()
            AppOpenAd.load(
                context,
                TEST_APP_OPEN_AD_ID,
                request,
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAdLoaded(ad: AppOpenAd) {
                        Log.d(TAG, "AppOpenAd loaded successfully.")
                        appOpenAd = ad
                        isAppOpenAdLoading = false
                        appOpenAdLoadTime = Date().time
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.w(TAG, "AppOpenAd failed to load: ${loadAdError.message}")
                        isAppOpenAdLoading = false
                        appOpenAd = null
                    }
                }
            )
        } catch (t: Throwable) {
            Log.e(TAG, "Exception loading AppOpenAd", t)
            isAppOpenAdLoading = false
            appOpenAd = null
        }
    }

    private fun isAppOpenAdAvailable(): Boolean {
        // App open ads expire after 4 hours
        val wasLoadedRecently = (Date().time - appOpenAdLoadTime) < (4 * 3600 * 1000)
        return appOpenAd != null && wasLoadedRecently
    }

    private fun showAppOpenAdIfAvailable(activity: Activity) {
        if (!BuildConfig.ENABLE_ADS || isShowingAppOpenAd || isShowingInterstitial) {
            return
        }

        if (!isAppOpenAdAvailable()) {
            loadAppOpenAd(activity.applicationContext)
            return
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                isShowingAppOpenAd = true
                Log.d(TAG, "AppOpenAd showed full screen content.")
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "AppOpenAd dismissed.")
                appOpenAd = null
                isShowingAppOpenAd = false
                loadAppOpenAd(activity.applicationContext)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.w(TAG, "AppOpenAd failed to show: ${adError.message}")
                appOpenAd = null
                isShowingAppOpenAd = false
                loadAppOpenAd(activity.applicationContext)
            }
        }

        appOpenAd?.show(activity)
    }

    // ProcessLifecycleOwner callback: triggers on foreground transition
    override fun onStart(owner: LifecycleOwner) {
        if (!BuildConfig.ENABLE_ADS) return
        isAppInForeground = true
        Log.d(TAG, "App entered foreground. isColdStart=$isColdStart")

        if (isColdStart) {
            // Cold start: DO NOT show App Open Ad as per strict user request!
            isColdStart = false
            currentActivity?.let { loadAppOpenAd(it.applicationContext) }
        } else {
            // Warm start: User returns from background to the app -> Show App Open Ad
            currentActivity?.let { activity ->
                showAppOpenAdIfAvailable(activity)
            }
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        isAppInForeground = false
        Log.d(TAG, "App went to background.")
    }

    // =========================================================================
    // INTERSTITIAL ADS & COUNTERS
    // =========================================================================

    private fun loadInterstitialAd(context: Context) {
        if (!BuildConfig.ENABLE_ADS || isInterstitialLoading || interstitialAd != null) {
            return
        }

        try {
            isInterstitialLoading = true
            val request = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                TEST_INTERSTITIAL_AD_ID,
                request,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        Log.d(TAG, "InterstitialAd loaded successfully.")
                        interstitialAd = ad
                        isInterstitialLoading = false
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.w(TAG, "InterstitialAd failed to load: ${loadAdError.message}")
                        interstitialAd = null
                        isInterstitialLoading = false
                    }
                }
            )
        } catch (t: Throwable) {
            Log.e(TAG, "Exception loading InterstitialAd", t)
            interstitialAd = null
            isInterstitialLoading = false
        }
    }

    /**
     * Tracks note clicks and edits.
     * Triggers an Interstitial Ad every 3 clicks/edits, then resets the counter.
     */
    fun trackNoteAction(activity: Activity?, onComplete: () -> Unit) {
        if (!BuildConfig.ENABLE_ADS) {
            onComplete()
            return
        }

        noteActionCount++
        Log.d(TAG, "Note action count: $noteActionCount / 3")

        if (noteActionCount >= 3) {
            noteActionCount = 0
            showInterstitialAd(activity, onComplete)
        } else {
            onComplete()
        }
    }

    /**
     * Tracks visits to sub-screens (Trash, AI Chat History, Settings).
     * Triggers an Interstitial Ad every 5 visits, then resets the counter.
     */
    fun trackSubScreenVisit(activity: Activity?) {
        if (!BuildConfig.ENABLE_ADS) return

        subScreenVisitCount++
        Log.d(TAG, "Sub-screen visit count: $subScreenVisitCount / 5")

        if (subScreenVisitCount >= 5) {
            subScreenVisitCount = 0
            showInterstitialAd(activity) {}
        }
    }

    private fun showInterstitialAd(activity: Activity?, onComplete: () -> Unit) {
        val targetActivity = activity ?: currentActivity

        if (targetActivity == null || interstitialAd == null || isShowingInterstitial || isShowingAppOpenAd) {
            targetActivity?.let { loadInterstitialAd(it.applicationContext) }
            onComplete()
            return
        }

        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                isShowingInterstitial = true
                Log.d(TAG, "InterstitialAd showed full screen content.")
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "InterstitialAd dismissed.")
                interstitialAd = null
                isShowingInterstitial = false
                targetActivity.let { loadInterstitialAd(it.applicationContext) }
                onComplete()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.w(TAG, "InterstitialAd failed to show: ${adError.message}")
                interstitialAd = null
                isShowingInterstitial = false
                targetActivity.let { loadInterstitialAd(it.applicationContext) }
                onComplete()
            }
        }

        interstitialAd?.show(targetActivity)
    }

    // =========================================================================
    // ACTIVITY LIFECYCLE TRACKING
    // =========================================================================

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity == activity) {
            currentActivity = null
        }
    }
}
