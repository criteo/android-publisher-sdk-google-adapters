package com.criteo.mediation.google.advancednative;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import com.criteo.mediation.google.ErrorCode;
import com.criteo.publisher.CriteoErrorCode;
import com.criteo.publisher.advancednative.CriteoNativeAd;
import com.criteo.publisher.advancednative.CriteoNativeAdListener;
import com.criteo.publisher.advancednative.NativeInternal;
import com.google.android.gms.ads.mediation.UnifiedNativeAdMapper;
import com.google.android.gms.ads.mediation.customevent.CustomEventNativeListener;
import java.lang.ref.WeakReference;
import java.util.Map;

public class CriteoNativeEventListener extends CriteoNativeAdListener {

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
        adMobListener.onAdLoaded(new CriteoUnifiedNativeAdMapper(contextRef.get(), nativeAd));
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

    private static class CriteoUnifiedNativeAdMapper extends UnifiedNativeAdMapper {

        private final WeakReference<CriteoNativeAd> nativeAdRef;

        CriteoUnifiedNativeAdMapper(@Nullable Context context, CriteoNativeAd nativeAd) {
            // Text fields
            setHeadline(nativeAd.getTitle());
            setBody(nativeAd.getDescription());
            setPrice(nativeAd.getPrice());
            setCallToAction(nativeAd.getCallToAction());
            setAdvertiser(nativeAd.getAdvertiserDescription());

            Bundle bundle = new Bundle();
            bundle.putString(CRT_NATIVE_ADV_DOMAIN, nativeAd.getAdvertiserDomain());
            setExtras(bundle);

            // TODO? setHasVideoContent(false);
            // TODO product media setMediaView();
            // TODO advertiser logo setIcon();

            // AdChoice
            if (context != null) {
                View renderedAd = nativeAd.createNativeRenderedView(context, null);
                View adChoiceView = NativeInternal.getAdChoiceView(nativeAd, renderedAd);
                if (adChoiceView != null) {
                    adChoiceView.setTag(AD_CHOICE_TAG);
                    setAdChoicesContent(adChoiceView);
                }
            }

            // Click & impression
            setOverrideClickHandling(true);
            setOverrideImpressionRecording(true);

            nativeAdRef = new WeakReference<>(nativeAd);
        }

        @Override
        public void trackViews(
            View containerView,
            Map<String, View> clickableAssetViews,
            Map<String, View> nonClickableAssetViews
        ) {
            CriteoNativeAd nativeAd = nativeAdRef.get();
            if (nativeAd != null) {
                // The renderer is expected to do nothing, but the SDK will start to watch this view
                // for clicks and impressions
                nativeAd.renderNativeView(containerView);

                // As the AdChoice icon is not injected by the SDK, we should explicitly set the
                // click listeners dedicated to AdChoice
                View adChoiceView = containerView.findViewWithTag(AD_CHOICE_TAG);
                if (adChoiceView != null) {
                    NativeInternal.setAdChoiceClickableView(nativeAd, adChoiceView);
                }
            }
        }
    }
}
