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

import static com.criteo.publisher.CriteoUtil.TEST_CP_ID;
import static com.criteo.publisher.concurrent.ThreadingUtil.runOnMainThreadAndWait;
import static org.mockito.Mockito.mock;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;
import com.criteo.publisher.model.AdSize;
import com.criteo.publisher.model.AdUnit;
import com.criteo.publisher.model.BannerAdUnit;
import com.criteo.publisher.model.InterstitialAdUnit;
import com.criteo.publisher.model.NativeAdUnit;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.NativeMediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener;
import com.google.android.gms.ads.mediation.customevent.CustomEventNativeListener;
import org.json.JSONException;
import org.json.JSONObject;

public class AdapterHelper {

  @NonNull
  private final CriteoAdapter adapter = new CriteoAdapter();

  @NonNull
  private final Context context = ApplicationProvider.getApplicationContext();

  @NonNull
  public final MediationAdRequest mediationAdRequest = mock(MediationAdRequest.class);

  @NonNull
  public final NativeMediationAdRequest nativeMediationAdRequest = mock(NativeMediationAdRequest.class);

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
        customEventExtras
    ));
  }

  public void requestBannerAd(
      @NonNull BannerAdUnit adUnit,
      @NonNull CustomEventBannerListener listener
  ) {
    String serverParameters = getServerParameters(adUnit);
    requestBannerAd(serverParameters, adUnit.getSize(), listener);
  }

  public void requestNativeAd(
      @NonNull String serverParameters,
      @NonNull CustomEventNativeListener listener
  ) {
    runOnMainThreadAndWait(() -> adapter.requestNativeAd(
        context,
        listener,
        serverParameters,
        nativeMediationAdRequest,
        customEventExtras
    ));
  }

  public void requestNativeAd(
      @NonNull NativeAdUnit adUnit,
      @NonNull CustomEventNativeListener listener
  ) {
    String serverParameters = getServerParameters(adUnit);
    requestNativeAd(serverParameters, listener);
  }

  public void requestInterstitialAd(
      @NonNull String serverParameters,
      @NonNull CustomEventInterstitialListener listener
  ) {
    runOnMainThreadAndWait(() -> adapter.requestInterstitialAd(
        context,
        listener,
        serverParameters,
        mediationAdRequest,
        customEventExtras
    ));
  }

  public void requestInterstitialAd(
      @NonNull InterstitialAdUnit adUnit,
      @NonNull CustomEventInterstitialListener listener
  ) {
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
