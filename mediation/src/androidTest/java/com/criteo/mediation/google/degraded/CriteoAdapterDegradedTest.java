package com.criteo.mediation.google.degraded;

import static com.criteo.mediation.google.CriteoHelper.TEST_CP_ID;
import static com.criteo.mediation.google.CriteoHelper.givenNotInitializedCriteo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import com.criteo.mediation.google.CriteoAdapter;
import com.criteo.mediation.google.CriteoHelper;
import com.criteo.publisher.CriteoAdListener;
import com.criteo.publisher.CriteoBannerAdListener;
import com.criteo.publisher.CriteoInterstitialAdListener;
import com.criteo.publisher.model.AdSize;
import com.criteo.publisher.model.AdUnit;
import com.criteo.publisher.model.BannerAdUnit;
import com.criteo.publisher.model.InterstitialAdUnit;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.AssumptionViolatedException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CriteoAdapterDegradedTest {

  private static final BannerAdUnit VALID_BANNER = new BannerAdUnit("test-PubSdk-Base",
      new AdSize(320, 50));

  private static final InterstitialAdUnit VALID_INTERSTITIAL = new InterstitialAdUnit("test-PubSdk-Interstitial");

  private Context context;

  @Mock
  private CustomEventBannerListener bannerListener;

  @Mock
  private CustomEventInterstitialListener interstitialListener;

  private CriteoAdapter adapter;

  @Before
  public void setUp() throws Exception {
    assumeIsDegraded();

    MockitoAnnotations.initMocks(this);
    context = InstrumentationRegistry.getContext();

    adapter = new CriteoAdapter();

    givenNotInitializedCriteo();
  }

  @Test
  public void loadBanner_GivenValidBannerAndLoadTwice_NotifyTwiceForNoFill() throws Exception {
    loadBanner(VALID_BANNER);
    loadBanner(VALID_BANNER);

    verify(bannerListener, times(2))
        .onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
  }

  @Test
  public void loadInterstitial_GivenValidInterstitialAndLoadTwice_NotifyTwiceForNoFill() throws Exception {
    loadInterstitial(VALID_INTERSTITIAL);
    loadInterstitial(VALID_INTERSTITIAL);

    verify(interstitialListener, times(2))
        .onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
  }

  private void loadBanner(BannerAdUnit adUnit) throws Exception {
    final String serverParameter = getServerParameters(adUnit);

    final com.google.android.gms.ads.AdSize adSize = new com.google.android.gms.ads.AdSize(
        adUnit.getSize().getWidth(),
        adUnit.getSize().getHeight()
    );

    final MediationAdRequest mediationAdRequest = mock(MediationAdRequest.class);
    final Bundle customEventExtras = new Bundle();

    InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
      @Override
      public void run() {
        adapter.requestBannerAd(
            context,
            bannerListener,
            serverParameter,
            adSize,
            mediationAdRequest,
            customEventExtras);
      }
    });

    waitForIdleState();
  }

  private void loadInterstitial(InterstitialAdUnit adUnit) throws Exception {
    final String serverParameter = getServerParameters(adUnit);
    final MediationAdRequest mediationAdRequest = mock(MediationAdRequest.class);
    final Bundle customEventExtras = new Bundle();

    InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
      @Override
      public void run() {
        adapter.requestInterstitialAd(
            context,
            interstitialListener,
            serverParameter,
            mediationAdRequest,
            customEventExtras);
      }
    });

    waitForIdleState();
  }

  private String getServerParameters(AdUnit adUnit) throws JSONException {
    JSONObject serverParams = new JSONObject();
    serverParams.put("cpId", TEST_CP_ID);
    serverParams.put("adUnitId", adUnit.getAdUnitId());
    return serverParams.toString();
  }

  private void waitForIdleState() throws InterruptedException {
    Thread.sleep(200);
  }

  private static void assumeIsDegraded() {
    if (VERSION.SDK_INT >= 19) {
      throw new AssumptionViolatedException(
          "Functionality is not degraded, version of device should be < 19");
    }
  }

}
