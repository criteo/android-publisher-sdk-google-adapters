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

import com.criteo.publisher.CriteoBannerView
import com.criteo.publisher.CriteoErrorCode
import com.criteo.publisher.model.AdSize
import com.criteo.publisher.model.BannerAdUnit
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.mediation.MediationAdLoadCallback
import com.google.android.gms.ads.mediation.MediationBannerAd
import com.google.android.gms.ads.mediation.MediationBannerAdCallback
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.check
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class CriteoBannerEventLoaderTest {

    @Mock
    private lateinit var mediationAdLoadCallback: MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback>

    @Mock
    private lateinit var mediationBannerAdCallback: MediationBannerAdCallback

    private lateinit var loader: CriteoBannerEventLoader

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        whenever(mediationAdLoadCallback.onSuccess(any())).thenReturn(
            mediationBannerAdCallback
        )
        loader = CriteoBannerEventLoader(
            mock(),
            mediationAdLoadCallback,
            BannerAdUnit("AdUnitId", AdSize(123, 123))
        )
    }

    @Test
    fun givenNoFillError_OnAdFailedToReceive_ReportToAdMobCallback() {
        loader.onAdFailedToReceive(CriteoErrorCode.ERROR_CODE_NO_FILL)

        verify(mediationAdLoadCallback).onFailure(check<AdError> {
            assertThat(it.code).isEqualTo(AdRequest.ERROR_CODE_NO_FILL)
        })
    }

    @Test
    fun onAdReceived_ReportToAdMobCallback() {
        loader.onAdReceived(mock())

        verify(mediationAdLoadCallback).onSuccess(any())
        verify(mediationBannerAdCallback).reportAdImpression()
    }

    @Test
    fun onAdReceivedAndOnAdLeftApplication_ReportToAdMobCallback() {
        loader.onAdReceived(mock())
        loader.onAdLeftApplication()

        verify(mediationBannerAdCallback).onAdLeftApplication()
    }

    @Test
    fun onAdReceivedAndOnAdClicked_ReportToAdMobCallback() {
        loader.onAdReceived(mock())
        loader.onAdClicked()

        verify(mediationBannerAdCallback).onAdOpened()
        verify(mediationBannerAdCallback).reportAdClicked()
    }

    @Test
    fun givenCriteoBannerView_OnAdReceivedAndGetBannerView_ShouldReturnSameViewAsInOnAdReceivedMethod() {
        val bannerView = mock<CriteoBannerView>()
        loader.onAdReceived(bannerView)

        val bannerFromLoader = loader.getView()

        assertThat(bannerFromLoader === bannerView).isTrue
    }
}