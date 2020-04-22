package com.criteo.mediation.google;

import static com.criteo.publisher.CriteoUtil.TEST_CP_ID;
import static com.criteo.publisher.concurrent.ThreadingUtil.runOnMainThreadAndWait;
import static org.mockito.Mockito.mock;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import com.criteo.publisher.model.AdSize;
import com.criteo.publisher.model.AdUnit;
import com.criteo.publisher.model.BannerAdUnit;
import com.criteo.publisher.model.InterstitialAdUnit;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener;
import org.json.JSONException;
import org.json.JSONObject;

public class AdapterHelper {

  @NonNull
  private final CriteoAdapter adapter = new CriteoAdapter();

  @NonNull
  private final Context context = InstrumentationRegistry.getContext();

  @NonNull
  private final MediationAdRequest mediationAdRequest = mock(MediationAdRequest.class);

  @NonNull
  private final Bundle customEventExtras = new Bundle();

  public void requestBannerAd(
      @NonNull String serverParameters,
      @NonNull AdSize size,
      @NonNull CustomEventBannerListener listener
  ) {
    com.google.android.gms.ads.AdSize adSize = new com.google.android.gms.ads.AdSize(
        size.getWidth(),
        size.getHeight()
    );

    runOnMainThreadAndWait(() -> adapter.requestBannerAd(
        context,
        listener,
        serverParameters,
        adSize,
        mediationAdRequest,
        customEventExtras));
  }

  public void requestBannerAd(
      @NonNull BannerAdUnit adUnit,
      @NonNull CustomEventBannerListener listener
  ) {
    String serverParameters = getServerParameters(adUnit);
    requestBannerAd(serverParameters, adUnit.getSize(), listener);
  }

  public void requestInterstitialAd(
      @NonNull String serverParameters,
      @NonNull CustomEventInterstitialListener listener) {
    runOnMainThreadAndWait(() -> adapter.requestInterstitialAd(
        context,
        listener,
        serverParameters,
        mediationAdRequest,
        customEventExtras));
  }

  public void requestInterstitialAd(
      @NonNull InterstitialAdUnit adUnit,
      @NonNull CustomEventInterstitialListener listener) {
    String serverParameters = getServerParameters(adUnit);
    requestInterstitialAd(serverParameters, listener);
  }

  private static String getServerParameters(AdUnit adUnit) {
    try {
      JSONObject serverParams = new JSONObject();
      serverParams.put("cpId", TEST_CP_ID);
      serverParams.put("adUnitId", adUnit.getAdUnitId());
      return serverParams.toString();
    } catch (JSONException e) {
      throw new IllegalStateException(e);
    }
  }


}
