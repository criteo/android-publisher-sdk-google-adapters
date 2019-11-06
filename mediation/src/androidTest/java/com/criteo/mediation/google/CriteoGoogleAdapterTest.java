package com.criteo.mediation.google;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.criteo.publisher.Criteo;
import com.criteo.publisher.CriteoBannerView;
import com.criteo.publisher.CriteoInitException;
import com.criteo.publisher.model.AdUnit;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@RunWith(AndroidJUnit4.class)
public class CriteoGoogleAdapterTest {

    private static final String CRITEO_PUBLISHER_ID_KEY = "cpId";
    private static final String ADUNITID_KEY = "adUnitId";
    private static final String PUBLISHER_ID = "B-056946";
    private static final String ADUNITID = "/140800857/Endeavour_Interstitial_320x480";
    private static final String BANNER_TEST_ADUNITID = "30s6zt3ayypfyemwjvmp";
    private static final String INTERSTITIAL_TEST_ADUNITID = "6yws53jyfjgoq1ghnuqb";

    private Context context;

    @Mock
    private CustomEventInterstitialListener interstitialListener;

    @Mock
    private CustomEventBannerListener bannerAdlistener;

    @Mock
    private MediationAdRequest mediationAdRequest;

    private Bundle customEventExtras;
    private CriteoAdapter criteoGoogleAdapter;


    @Before
    public void setUp() {
        context = InstrumentationRegistry.getContext();
        criteoGoogleAdapter = new CriteoAdapter();
        MockitoAnnotations.initMocks(this);
        customEventExtras = new Bundle();
    }

    @After
    public void tearDown() {
        context = null;
        criteoGoogleAdapter = null;
        customEventExtras = null;
    }

    @Test
    public void requestBannerAdWithEmptyServerParams() {
        String serverParameter = "";
        criteoGoogleAdapter
                .requestBannerAd(context, bannerAdlistener, serverParameter, new AdSize(320, 480), mediationAdRequest,
                        customEventExtras);
        Mockito.verify(bannerAdlistener, Mockito.times(1)).onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
    }

    @Test
    public void requestBannerAdWithNullCriteo() {
        String serverParameter = "{   \"" + CRITEO_PUBLISHER_ID_KEY + "\":" + PUBLISHER_ID + ",   \" " + ADUNITID_KEY
                + "\":\" " + ADUNITID + "  \" }";
        criteoGoogleAdapter
                .requestBannerAd(context, bannerAdlistener, serverParameter, new AdSize(320, 480), mediationAdRequest,
                        customEventExtras);
        Mockito.verify(bannerAdlistener, Mockito.times(1)).onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR);

    }

    @Test
    public void requestInterstitialAdWithEmptyServerParams() {
        String serverParameter = "";
        criteoGoogleAdapter
                .requestInterstitialAd(context, interstitialListener, serverParameter, mediationAdRequest,
                        customEventExtras);
        Mockito.verify(interstitialListener, Mockito.times(1)).onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);

    }

    @Test
    public void requestInterstitialAdWithNullCriteo() {
        String serverParameter = "{   \"" + CRITEO_PUBLISHER_ID_KEY + "\":" + PUBLISHER_ID + ",   \" " + ADUNITID_KEY
                + "\":\" " + ADUNITID + "  \" }";
        criteoGoogleAdapter
                .requestInterstitialAd(context, interstitialListener, serverParameter, mediationAdRequest,
                        customEventExtras);
        Mockito.verify(interstitialListener, Mockito.times(1)).onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR);

    }

    @Test
    public void givenNotInitializedCriteo_WhenLoadingBannerTwice_MissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne()
            throws Exception {
        CriteoHelper.givenNotInitializedCriteo();

        whenLoadingTwice(new TestBannerLoader());

        checkMissFirstBannerOpportunityBecauseOfBidCachingAndSucceedOnNextOne();
    }

    @Test
    public void givenInitializedCriteo_WhenLoadingBannerTwice_MissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne()
            throws Exception {
        givenInitializedEmptyCriteo();

        whenLoadingTwice(new TestBannerLoader());

        checkMissFirstBannerOpportunityBecauseOfBidCachingAndSucceedOnNextOne();
    }

    @Test
    public void givenNotInitializedCriteo_WhenLoadingInterstitialTwice_MissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne()
          throws Exception {
        CriteoHelper.givenNotInitializedCriteo();

        whenLoadingTwice(new TestInterstitialLoader());

        checkMissFirstInterstitialOpportunityBecauseOfBidCachingAndSucceedOnNextOne();
    }

    @Test
    public void givenInitializedCriteo_WhenLoadingInterstitialTwice_MissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne()
            throws Exception {
        givenInitializedEmptyCriteo();

        whenLoadingTwice(new TestInterstitialLoader());

        checkMissFirstInterstitialOpportunityBecauseOfBidCachingAndSucceedOnNextOne();
    }

    private void givenInitializedEmptyCriteo()
        throws ReflectiveOperationException, CriteoInitException {
        // Clean the cache state first.
        CriteoHelper.givenNotInitializedCriteo();

        Application application = (Application) context.getApplicationContext();
        List<AdUnit> adUnits = Collections.emptyList();
        Criteo.init(application, PUBLISHER_ID, adUnits);
    }

    private void whenLoadingTwice(final Runnable loadingOnce) throws Exception {
        final CyclicBarrier latch = new CyclicBarrier(2);

        Runnable loadBanner = new Runnable() {
            @Override
            public void run() {
                loadingOnce.run();

                try {
                    latch.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        };

        final Handler handler = new Handler(Looper.getMainLooper());

        handler.post(loadBanner);
        latch.await();
        Thread.sleep(5000);

        handler.post(loadBanner);
        latch.await();
        Thread.sleep(2000);
    }

    private void checkMissFirstBannerOpportunityBecauseOfBidCachingAndSucceedOnNextOne() {
        InOrder inOrder = inOrder(bannerAdlistener);
        inOrder.verify(bannerAdlistener).onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
        inOrder.verify(bannerAdlistener).onAdLoaded(any(CriteoBannerView.class));
        inOrder.verifyNoMoreInteractions();
    }

    private void checkMissFirstInterstitialOpportunityBecauseOfBidCachingAndSucceedOnNextOne() {
        InOrder inOrder = inOrder(interstitialListener);
        inOrder.verify(interstitialListener).onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
        inOrder.verify(interstitialListener).onAdLoaded();
        inOrder.verifyNoMoreInteractions();
    }

    private class TestBannerLoader implements Runnable {
        @Override
        public void run() {
            String serverParameter = "{"
                + "\"" + CRITEO_PUBLISHER_ID_KEY + "\": \"" + PUBLISHER_ID + "\","
                + "\"" + ADUNITID_KEY + "\": \"" + BANNER_TEST_ADUNITID + "\""
                + "}";
            AdSize size = new AdSize(320, 480);

            criteoGoogleAdapter.requestBannerAd(context, bannerAdlistener, serverParameter,
                size, mediationAdRequest, customEventExtras);
        }
    }

    private class TestInterstitialLoader implements Runnable {
        @Override
        public void run() {
            String serverParameter = "{"
                + "\"" + CRITEO_PUBLISHER_ID_KEY + "\": \"" + PUBLISHER_ID + "\","
                + "\"" + ADUNITID_KEY + "\": \"" + INTERSTITIAL_TEST_ADUNITID + "\""
                + "}";

            criteoGoogleAdapter.requestInterstitialAd(context, interstitialListener, serverParameter,
                mediationAdRequest, customEventExtras);
        }
    }
}
