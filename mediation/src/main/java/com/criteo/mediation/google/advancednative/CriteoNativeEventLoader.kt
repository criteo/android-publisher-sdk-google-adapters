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

package com.criteo.mediation.google.advancednative;

import static com.criteo.mediation.google.PreconditionsUtil.isNotNull;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import com.criteo.mediation.google.ErrorCode;
import com.criteo.publisher.CriteoErrorCode;
import com.criteo.publisher.advancednative.CriteoMediaView;
import com.criteo.publisher.advancednative.CriteoNativeAd;
import com.criteo.publisher.advancednative.CriteoNativeAdListener;
import com.criteo.publisher.advancednative.CriteoNativeRenderer;
import com.criteo.publisher.advancednative.NativeInternalForAdMob;
import com.criteo.publisher.advancednative.RendererHelper;
import com.google.android.gms.ads.mediation.UnifiedNativeAdMapper;
import com.google.android.gms.ads.mediation.customevent.CustomEventNativeListener;
import java.lang.ref.WeakReference;
import java.util.Map;

public class CriteoNativeEventListener implements CriteoNativeAdListener {

    private static final String CRT_NATIVE_ADV_DOMAIN = "crtn_advdomain";

    @VisibleForTesting
    public static final Object AD_CHOICE_TAG = new Object();

    private final WeakReference<Context> contextRef;

    private final CustomEventNativeListener adMobListener;

    public CriteoNativeEventListener(
        Context context,
        CustomEventNativeListener adMobListener
    ) {
        this.contextRef = new WeakReference<>(context);
        this.adMobListener = adMobListener;
    }

    @Override
    public void onAdReceived(@NonNull CriteoNativeAd nativeAd) {
        adMobListener.onAdLoaded(new CriteoUnifiedNativeAdMapper(contextRef.get(), nativeAd, this));
    }

    @Override
    public void onAdFailedToReceive(@NonNull CriteoErrorCode errorCode) {
        adMobListener.onAdFailedToLoad(ErrorCode.toAdMob(errorCode));
    }

    @Override
    public void onAdClicked() {
        adMobListener.onAdClicked();
    }

    @Override
    public void onAdImpression() {
        adMobListener.onAdImpression();
    }

    @Override
    public void onAdLeftApplication() {
        adMobListener.onAdOpened();
        adMobListener.onAdLeftApplication();
    }

    @Override
    public void onAdClosed() {
        adMobListener.onAdClosed();
    }

    private static class CriteoUnifiedNativeAdMapper extends UnifiedNativeAdMapper {

        private final CriteoNativeAd nativeAd;

        /**
         * Hold the listener until the end of life of this ad
         * <p>
         * Normally it is the job of the native loader to hold the listener. But in case of this
         * adapter, the loader is thrown directly and nothing prevent the listener to be GC. So it is
         * hold here.
         */
        @Keep
        @NonNull
        private final CriteoNativeAdListener listener;

        CriteoUnifiedNativeAdMapper(
            @Nullable Context context,
            @NonNull CriteoNativeAd nativeAd,
            @NonNull CriteoNativeAdListener listener
        ) {
            this.listener = listener;

            // Text fields
            setHeadline(nativeAd.getTitle());
            setBody(nativeAd.getDescription());
            setPrice(nativeAd.getPrice());
            setCallToAction(nativeAd.getCallToAction());
            setAdvertiser(nativeAd.getAdvertiserDescription());

            Bundle bundle = new Bundle();
            bundle.putString(CRT_NATIVE_ADV_DOMAIN, nativeAd.getAdvertiserDomain());
            setExtras(bundle);

            if (context != null) {
                MediaAndLogoRenderer mediaAndLogoRenderer = new MediaAndLogoRenderer();
                NativeInternalForAdMob.setRenderer(nativeAd, mediaAndLogoRenderer);
                // createNativeRenderedView calls both createNativeView and renderNativeView of the
                // renderer, so images are now currently being loaded
                View nativeRenderedView = nativeAd.createNativeRenderedView(context, null);

                // Product media
                setMediaView(mediaAndLogoRenderer.getProductMediaView());
                setHasVideoContent(false);

                // Advertiser logo
                CriteoMediaView iconCriteoMediaView = mediaAndLogoRenderer.getAdvertiserLogoView();
                if (isNotNull(iconCriteoMediaView)) {
                    IconNativeAdImage iconImage = IconNativeAdImage.create(
                        iconCriteoMediaView,
                        nativeAd.getAdvertiserLogoMedia()
                    );
                    setIcon(iconImage);
                }

                // AdChoice
                View adChoiceView = NativeInternalForAdMob.getAdChoiceView(nativeAd, nativeRenderedView);
                if (isNotNull(adChoiceView)) {
                    adChoiceView.setTag(AD_CHOICE_TAG);
                    setAdChoicesContent(adChoiceView);
                }
            }

            // Click & impression
            setOverrideClickHandling(true);
            setOverrideImpressionRecording(true);

            this.nativeAd = nativeAd;
        }

        @Override
        public void trackViews(
            View containerView,
            Map<String, View> clickableAssetViews,
            Map<String, View> nonClickableAssetViews
        ) {
                // The renderer is expected to do nothing, but the SDK will start to watch this view
                // for clicks and impressions
                NativeInternalForAdMob.setRenderer(nativeAd, new NoOpNativeRenderer());
                nativeAd.renderNativeView(containerView);

                // As the AdChoice icon is not injected by the SDK, we should explicitly set the
                // click listeners dedicated to AdChoice
                View adChoiceView = containerView.findViewWithTag(AD_CHOICE_TAG);
                if (adChoiceView != null) {
                    NativeInternalForAdMob.setAdChoiceClickableView(nativeAd, adChoiceView);
                }
            }
    }

    private static class MediaAndLogoRenderer implements CriteoNativeRenderer {

        @Nullable
        private CriteoMediaView productMediaView;

        @Nullable
        private CriteoMediaView advertiserLogoView;

        @Nullable
        public CriteoMediaView getProductMediaView() {
            return productMediaView;
        }

        @Nullable
        public CriteoMediaView getAdvertiserLogoView() {
            return advertiserLogoView;
        }

        @NonNull
        @Override
        public View createNativeView(@NonNull Context context, @Nullable ViewGroup parent) {
            productMediaView = new CriteoMediaView(context);
            advertiserLogoView = new CriteoMediaView(context);
            return new View(context);
        }

        @Override
        public void renderNativeView(
            @NonNull RendererHelper helper,
            @NonNull View nativeView,
            @NonNull CriteoNativeAd nativeAd
        ) {
            if (isNotNull(productMediaView)) {
                helper.setMediaInView(nativeAd.getProductMedia(), productMediaView);
            }
            if (isNotNull(advertiserLogoView)) {
                helper.setMediaInView(nativeAd.getAdvertiserLogoMedia(), advertiserLogoView);
            }
        }
    }
}
