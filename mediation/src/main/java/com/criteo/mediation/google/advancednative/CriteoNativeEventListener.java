package com.criteo.mediation.google.advancednative;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.criteo.publisher.CriteoErrorCode;
import com.criteo.publisher.advancednative.CriteoNativeAd;
import com.criteo.publisher.advancednative.CriteoNativeAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.UnifiedNativeAdMapper;
import com.google.android.gms.ads.mediation.customevent.CustomEventNativeListener;

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
        // TODO map error code
        adMobListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
    }

    private static class CriteoUnifiedNativeAdMapper extends UnifiedNativeAdMapper {

        CriteoUnifiedNativeAdMapper(CriteoNativeAd nativeAd) {
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
            // TODO click
            // TODO impression
        }
    }
}
