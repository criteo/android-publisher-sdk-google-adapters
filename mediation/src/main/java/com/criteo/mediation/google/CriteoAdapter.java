/*
 *    Copyright 2020 Criteo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;

public class CriteoAdapter
        implements CustomEventBanner, CustomEventInterstitial, CustomEventNative {

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
        if (initialize(context, serverParameter, size, FormatType.BANNER, listener, getTagForChildDirectedTreatment(mediationAdRequest))) {
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
        if (initialize(context, serverParameter, null, FormatType.INTERSTITIAL, listener, getTagForChildDirectedTreatment(mediationAdRequest))) {
            criteoInterstitial = new CriteoInterstitial(interstitialAdUnit);
            CriteoInterstitialEventListener criteoInterstitialEventListener = new CriteoInterstitialEventListener(
                listener);
            criteoInterstitial.setCriteoInterstitialAdListener(criteoInterstitialEventListener);

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
        if (initialize(context, serverParameter, null, FormatType.NATIVE, listener, getTagForChildDirectedTreatment(nativeMediationAdRequest))) {
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
        @NonNull CustomEventListener listener,
        @Nullable Boolean tagForChildDirectedTreatment
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
            Criteo.getInstance().setTagForChildDirectedTreatment(tagForChildDirectedTreatment);
            return true;
        } catch (Exception ex) {
            try {
                new Criteo.Builder((Application) context.getApplicationContext(), criteoPublisherId)
                    .adUnits(Collections.singletonList(adUnit))
                    .tagForChildDirectedTreatment(tagForChildDirectedTreatment)
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

    @Nullable
    private Boolean getTagForChildDirectedTreatment(MediationAdRequest request) {
        switch (request.taggedForChildDirectedTreatment()) {
            case MediationAdRequest.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE:
                return true;
            case MediationAdRequest.TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE:
                return false;
            default:
                return null;
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
