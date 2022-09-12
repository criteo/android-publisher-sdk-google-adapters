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

package com.criteo.mediation.google.degraded;

import static com.criteo.publisher.CriteoUtil.clearCriteo;
import static com.criteo.publisher.TestAdUnits.BANNER_320_50;
import static com.criteo.publisher.TestAdUnits.INTERSTITIAL;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.criteo.mediation.google.AdErrorKt;
import com.criteo.mediation.google.AdapterHelper;
import com.criteo.mediation.google.IsEqualToOtherAdError;
import com.criteo.publisher.mock.MockedDependenciesRule;
import com.criteo.publisher.mock.SpyBean;
import com.criteo.publisher.model.BannerAdUnit;
import com.criteo.publisher.model.InterstitialAdUnit;
import com.criteo.publisher.util.DeviceUtil;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationBannerAd;
import com.google.android.gms.ads.mediation.MediationBannerAdCallback;
import com.google.android.gms.ads.mediation.MediationInterstitialAd;
import com.google.android.gms.ads.mediation.MediationInterstitialAdCallback;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CriteoAdapterDegradedTest {

  @Rule
  public MockedDependenciesRule mockedDependenciesRule = new MockedDependenciesRule();

  @Mock
  private MediationAdLoadCallback<MediationInterstitialAd, MediationInterstitialAdCallback> interstitialCallback;

  @Mock
  private MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback> bannerCallback;

  @SpyBean
  private DeviceUtil deviceUtil;

  private AdapterHelper adapterHelper;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    assumeIsDegraded();

    adapterHelper = new AdapterHelper();

    clearCriteo();
  }

  @Test
  public void loadBanner_GivenValidBannerAndLoadTwice_NotifyTwiceForNoFill() throws Exception {
    loadBanner(BANNER_320_50);
    loadBanner(BANNER_320_50);

    verify(bannerCallback, times(2))
        .onFailure(argThat(new IsEqualToOtherAdError(AdErrorKt.noFillError())));
  }

  @Test
  public void loadInterstitial_GivenValidInterstitialAndLoadTwice_NotifyTwiceForNoFill() throws Exception {
    loadInterstitial(INTERSTITIAL);
    loadInterstitial(INTERSTITIAL);

    verify(interstitialCallback, times(2))
        .onFailure(argThat(new IsEqualToOtherAdError(AdErrorKt.noFillError())));
  }

  private void loadBanner(BannerAdUnit adUnit) {
    adapterHelper.loadBannerAd(adUnit, bannerCallback);
    waitForIdleState();
  }

  private void loadInterstitial(InterstitialAdUnit adUnit) {
    adapterHelper.loadInterstitialAd(adUnit, interstitialCallback);
    waitForIdleState();
  }

  private void waitForIdleState() {
    mockedDependenciesRule.waitForIdleState();
  }

  private void assumeIsDegraded() {
    when(deviceUtil.isVersionSupported()).thenReturn(false);
  }

}
