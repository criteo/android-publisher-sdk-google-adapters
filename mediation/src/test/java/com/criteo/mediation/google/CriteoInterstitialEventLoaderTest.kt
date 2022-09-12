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

package com.criteo.mediation.google

import com.criteo.publisher.CriteoErrorCode
import com.criteo.publisher.CriteoInterstitial
import com.criteo.publisher.model.InterstitialAdUnit
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.mediation.MediationAdLoadCallback
import com.google.android.gms.ads.mediation.MediationInterstitialAd
import com.google.android.gms.ads.mediation.MediationInterstitialAdCallback
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.check
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class CriteoInterstitialEventLoaderTest {

    @Mock
    private lateinit var mediationAdLoadCallback: MediationAdLoadCallback<MediationInterstitialAd, MediationInterstitialAdCallback>

    @Mock
    private lateinit var mediationInterstitialAdCallback: MediationInterstitialAdCallback

    private lateinit var loader: CriteoInterstitialEventLoader

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        whenever(mediationAdLoadCallback.onSuccess(any())).thenReturn(
            mediationInterstitialAdCallback
        )
        loader = CriteoInterstitialEventLoader(
            mediationAdLoadCallback,
            InterstitialAdUnit("AdUnitId")
        )
    }

    @Test
    fun givenNoFillError_OnAdFailedToReceive_ReportToAdMobCallback() {
        loader.onAdFailedToReceive(CriteoErrorCode.ERROR_CODE_NO_FILL)

        verify(mediationAdLoadCallback).onFailure(check<AdError> {
            Assertions.assertThat(it.code).isEqualTo(AdRequest.ERROR_CODE_NO_FILL)
        })
    }

    @Test
    fun onAdReceived_ReportToAdMobCallback() {
        loader.onAdReceived(mock())

        verify(mediationAdLoadCallback).onSuccess(any())
    }

    @Test
    fun onAdReceivedAndOnAdOpened_ReportToAdMobCallback() {
        loader.onAdReceived(mock())
        loader.onAdOpened()

        verify(mediationInterstitialAdCallback).reportAdImpression()
        verify(mediationInterstitialAdCallback).onAdOpened()
    }

    @Test
    fun onAdReceivedAndOnAdClosed_ReportToAdMobCallback() {
        loader.onAdReceived(mock())
        loader.onAdClosed()

        verify(mediationInterstitialAdCallback).onAdClosed()
    }

    @Test
    fun onAdReceivedAndOnAdLeftApplication_ReportToAdMobCallback() {
        loader.onAdReceived(mock())
        loader.onAdLeftApplication()

        verify(mediationInterstitialAdCallback).onAdLeftApplication()
    }

    @Test
    fun onAdReceivedAndOnAdClicked_ReportToAdMobCallback() {
        loader.onAdReceived(mock())
        loader.onAdClicked()

        verify(mediationInterstitialAdCallback).reportAdClicked()
    }

    @Test
    fun onAdReceivedAndShowAd_ShouldCallShowOnCriteoInterstitial() {
        val criteoInterstitial = mock<CriteoInterstitial>()
        loader.onAdReceived(criteoInterstitial)

        loader.showAd(mock())
        verify(criteoInterstitial).show()
    }
}