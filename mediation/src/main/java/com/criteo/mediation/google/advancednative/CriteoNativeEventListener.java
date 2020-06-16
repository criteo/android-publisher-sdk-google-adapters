package com.criteo.mediation.google.advancednative;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import com.criteo.mediation.google.ErrorCode;
import com.criteo.publisher.CriteoErrorCode;
import com.criteo.publisher.advancednative.CriteoNativeAd;
import com.criteo.publisher.advancednative.CriteoNativeAdListener;
import com.google.android.gms.ads.mediation.UnifiedNativeAdMapper;
import com.google.android.gms.ads.mediation.customevent.CustomEventNativeListener;
import java.lang.ref.WeakReference;
import java.util.Map;

public class CriteoNativeEventListener extends CriteoNativeAdListener {

    private static final String CRT_NATIVE_ADV_DOMAIN = "crtn_advdomain";

    private final CustomEventNativeListener adMobListener;

    public CriteoNativeEventListener(CustomEventNativeListener adMobListener) {
        this.adMobListener = adMobListener;
    }

    @Override
    public void onAdReceived(@NonNull CriteoNativeAd nativeAd) {
        adMobListener.onAdLoaded(new CriteoUnifiedNativeAdMapper(nativeAd));
    }

    @Override
    public void onAdFailedToReceive(@NonNull CriteoErrorCode errorCode) {
        adMobListener.onAdFailedToLoad(ErrorCode.toAdMob(errorCode));
    }

    @Override
    public void onAdClicked() {
        adMobListener.onAdClicked();
    }

    private static class CriteoUnifiedNativeAdMapper extends UnifiedNativeAdMapper {

        private final WeakReference<CriteoNativeAd> nativeAdRef;

        CriteoUnifiedNativeAdMapper(CriteoNativeAd nativeAd) {
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
            // TODO setAdChoicesContent();

            // Click
            setOverrideClickHandling(true);

            // TODO impression

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
            }
        }
    }
}
