package com.criteo.mediation.google.advancednative;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import com.criteo.mediation.google.ErrorCode;
import com.criteo.publisher.CriteoErrorCode;
import com.criteo.publisher.advancednative.CriteoMediaView;
import com.criteo.publisher.advancednative.CriteoNativeAd;
import com.criteo.publisher.advancednative.CriteoNativeAdListener;
import com.criteo.publisher.advancednative.CriteoNativeRenderer;
import com.criteo.publisher.advancednative.NativeInternal;
import com.criteo.publisher.advancednative.RendererHelper;
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

            // TODO advertiser logo setIcon();

            if (context != null) {
                // Product media
                ProductMediaRenderer renderer = new ProductMediaRenderer();
                NativeInternal.setRenderer(nativeAd, renderer);
                View renderedAd = nativeAd.createNativeRenderedView(context, null);
                setMediaView(renderer.getLastProductMediaView());
                setHasVideoContent(false);

                // AdChoice
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
                NativeInternal.setRenderer(nativeAd, new NoOpNativeRenderer());
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

    private static class ProductMediaRenderer implements CriteoNativeRenderer {

        @Nullable
        private CriteoMediaView lastProductMediaView;

        @Nullable
        public CriteoMediaView getLastProductMediaView() {
            return lastProductMediaView;
        }

        @NonNull
        @Override
        public View createNativeView(@NonNull Context context, @Nullable ViewGroup parent) {
            lastProductMediaView = new CriteoMediaView(context);
            return lastProductMediaView;
        }

        @Override
        public void renderNativeView(
            @NonNull RendererHelper helper,
            @NonNull View nativeView,
            @NonNull CriteoNativeAd nativeAd
        ) {
            if (lastProductMediaView != null) {
                helper.setMediaInView(nativeAd.getProductMedia(), lastProductMediaView);
            }
        }
    }
}
