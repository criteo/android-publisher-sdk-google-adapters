package com.criteo.mediation.google;


import android.view.View;
import com.criteo.publisher.CriteoBannerAdListener;
import com.criteo.publisher.CriteoErrorCode;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;

public class CriteoBannerEventListener implements CriteoBannerAdListener {

    private final CustomEventBannerListener customEventBannerListener;

    public CriteoBannerEventListener(CustomEventBannerListener listener) {
        customEventBannerListener = listener;
    }

    @Override
    public void onAdReceived(View view) {
        customEventBannerListener.onAdLoaded(view);
    }

    @Override
    public void onAdFailedToReceive(CriteoErrorCode code) {
        customEventBannerListener.onAdFailedToLoad(ErrorCode.toAdMob(code));
    }

    @Override
    public void onAdLeftApplication() {
        customEventBannerListener.onAdLeftApplication();
    }

    @Override
    public void onAdClicked() {
        customEventBannerListener.onAdClicked();
    }

    @Override
    public void onAdOpened() {
        customEventBannerListener.onAdOpened();
    }

    @Override
    public void onAdClosed() {
        customEventBannerListener.onAdClosed();
    }

}
