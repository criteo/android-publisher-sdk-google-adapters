package com.criteo.mediation.google;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.criteo.mediation.google.advancednative.CriteoNativeEventListener;
import com.criteo.mediation.google.advancednative.NoOpNativeRenderer;
import com.criteo.publisher.Criteo;
import com.criteo.publisher.CriteoBannerAdListener;
import com.criteo.publisher.CriteoBannerView;
import com.criteo.publisher.CriteoInitException;
import com.criteo.publisher.CriteoInterstitial;
import com.criteo.publisher.advancednative.CriteoNativeLoader;
import com.criteo.publisher.model.AdUnit;
import com.criteo.publisher.model.BannerAdUnit;
import com.criteo.publisher.model.InterstitialAdUnit;
import com.criteo.publisher.model.NativeAdUnit;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.NativeMediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventBanner;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitial;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener;
import com.google.android.gms.ads.mediation.customevent.CustomEventListener;
import com.google.android.gms.ads.mediation.customevent.CustomEventNative;
import com.google.android.gms.ads.mediation.customevent.CustomEventNativeListener;
import java.util.Collections;
import org.json.JSONException;
import org.json.JSONObject;

public class CriteoAdapter implements CustomEventBanner, CustomEventInterstitial, CustomEventNative {

    protected static final String TAG = CriteoAdapter.class.getSimpleName();

    protected static final String CRITEO_PUBLISHER_ID = "cpId";
    protected static final String AD_UNIT_ID = "adUnitId";

    private CriteoInterstitial criteoInterstitial;
    private BannerAdUnit bannerAdUnit;
    private InterstitialAdUnit interstitialAdUnit;
    private NativeAdUnit nativeAdUnit;

    private enum FormatType {
        BANNER,
        INTERSTITIAL,
        NATIVE
    }

    /**
     * The app requested a banner ad
     */
    @Override
    public void requestBannerAd(Context context,
            CustomEventBannerListener listener,
            String serverParameter,
            AdSize size,
            MediationAdRequest mediationAdRequest,
            Bundle customEventExtras) {
        if (initialize(context, serverParameter, size, FormatType.BANNER, listener)) {
            CriteoBannerView criteoBanner = new CriteoBannerView(context, bannerAdUnit);
            CriteoBannerAdListener criteoBannerAdListener = new CriteoBannerEventListener(listener);
            criteoBanner.setCriteoBannerAdListener(criteoBannerAdListener);
            criteoBanner.loadAd();
        }
    }

    /**
     * The app requested an interstitial ad
     */
    @Override
    public void requestInterstitialAd(Context context,
            CustomEventInterstitialListener listener,
            String serverParameter,
            MediationAdRequest mediationAdRequest,
            Bundle customEventExtras) {
        if (initialize(context, serverParameter, null, FormatType.INTERSTITIAL, listener)) {
            criteoInterstitial = new CriteoInterstitial(context, interstitialAdUnit);
            CriteoInterstitialEventListener criteoInterstitialEventListener = new CriteoInterstitialEventListener(
                listener);
            criteoInterstitial.setCriteoInterstitialAdListener(criteoInterstitialEventListener);
            criteoInterstitial.setCriteoInterstitialAdDisplayListener(criteoInterstitialEventListener);

            criteoInterstitial.loadAd();
        }
    }

    @Override
    public void showInterstitial() {
        // Show your interstitial ad
        if (criteoInterstitial != null) {
            criteoInterstitial.show();
        }
    }

    @Override
    public void requestNativeAd(
        @NonNull Context context,
        @NonNull CustomEventNativeListener listener,
        @Nullable String serverParameter,
        @Nullable NativeMediationAdRequest nativeMediationAdRequest,
        @Nullable Bundle bundle
    ) {
        if (initialize(context, serverParameter, null, FormatType.NATIVE, listener)) {
            CriteoNativeLoader loader = new CriteoNativeLoader(
                nativeAdUnit,
                new CriteoNativeEventListener(context, listener),
                new NoOpNativeRenderer()
            );

            loader.loadAd();
        }
    }

    private boolean initialize(
        @NonNull Context context,
        @Nullable String serverParameter,
        @Nullable AdSize size,
        @NonNull FormatType formatType,
        @NonNull CustomEventListener listener
    ) {
        if (TextUtils.isEmpty(serverParameter)) {
            listener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            Log.e(TAG, "Server parameter was empty.");
            return false;
        }

        String criteoPublisherId;
        String adUnitId;
        try {
            JSONObject parameters = new JSONObject(serverParameter);
            criteoPublisherId = parameters.getString(CRITEO_PUBLISHER_ID);
            adUnitId = parameters.getString(AD_UNIT_ID);
        } catch (JSONException e) {
            listener.onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR);
            Log.e(TAG, "Adapter failed to read server parameters", e);
            return false;
        }

        AdUnit adUnit = initAdUnit(formatType, adUnitId, size);

        try {
            //noinspection ResultOfMethodCallIgnored
            Criteo.getInstance();
            return true;
        } catch (Exception ex) {
            try {
                new Criteo.Builder((Application) context.getApplicationContext(), criteoPublisherId)
                    .adUnits(Collections.singletonList(adUnit))
                    .init();
            } catch (CriteoInitException e) {
                listener.onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR);
                Log.e(TAG, "Adapter failed to initialize", e);
                return false;
            }

            listener.onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
            return false;
        }
    }

    private AdUnit initAdUnit(
        @NonNull FormatType formatType,
        @NonNull String adUnitId,
        @Nullable AdSize size
    ) {
        switch (formatType) {
            case BANNER:
                com.criteo.publisher.model.AdSize adMobSize = new com.criteo.publisher.model.AdSize(
                    size.getWidth(),
                    size.getHeight()
                );
                return bannerAdUnit = new BannerAdUnit(adUnitId, adMobSize);
            case INTERSTITIAL:
                return interstitialAdUnit = new InterstitialAdUnit(adUnitId);
            case NATIVE:
                return nativeAdUnit = new NativeAdUnit(adUnitId);
            default:
                throw new UnsupportedOperationException("Unknown format: " + formatType);
        }
    }

    /**
     * The event is being destroyed. Perform any necessary cleanup here.
     */
    @Override
    public void onDestroy() {
    }

    /**
     * The app is being paused. This call will only be forwarded to the adapter if the developer notifies mediation that
     * the app is being paused.
     */
    @Override
    public void onPause() {
    }

    /**
     * The app is being resumed. This call will only be forwarded to the adapter if the developer notifies mediation
     * that the app is being resumed.
     */
    @Override
    public void onResume() {
    }
}
