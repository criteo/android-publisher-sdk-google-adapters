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

package com.criteo.mediation.google.advancednative

import com.criteo.publisher.CriteoErrorCode
import com.criteo.publisher.model.NativeAdUnit
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.mediation.MediationAdLoadCallback
import com.google.android.gms.ads.mediation.MediationNativeAdCallback
import com.google.android.gms.ads.mediation.UnifiedNativeAdMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.kotlin.check

class CriteoNativeEventLoaderTest {

    @Mock
    private lateinit var mediationAdLoadCallback: MediationAdLoadCallback<UnifiedNativeAdMapper, MediationNativeAdCallback>

    @Mock
    private lateinit var mediationNativeAdCallback: MediationNativeAdCallback

    private lateinit var loader: CriteoNativeEventLoader

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        whenever(mediationAdLoadCallback.onSuccess(any())).thenReturn(mediationNativeAdCallback)
        loader = CriteoNativeEventLoader(mock(), mediationAdLoadCallback, NativeAdUnit("AdUnitId"))
    }

    @Test
    fun givenNoFillError_OnAdFailedToReceive_ReportToAdMobCallback() {
        loader.onAdFailedToReceive(CriteoErrorCode.ERROR_CODE_NO_FILL)

        verify(mediationAdLoadCallback).onFailure(check<AdError> {
            assertThat(it.code).isEqualTo(AdRequest.ERROR_CODE_NO_FILL)
        })
    }

    @Test
    fun givenInternalError_OnAdFailedToReceive_ReportToAdMobCallback() {
        loader.onAdFailedToReceive(CriteoErrorCode.ERROR_CODE_INTERNAL_ERROR)

        verify(mediationAdLoadCallback).onFailure(check<AdError> {
            assertThat(it.code).isEqualTo(AdRequest.ERROR_CODE_INTERNAL_ERROR)
        })
    }

    @Test
    fun onAdReceived_ReportToAdMobCallback() {
        loader.onAdReceived(mock())

        verify(mediationAdLoadCallback).onSuccess(any())
    }

    @Test
    fun onAdReceivedAndOnAdClicked_ReportToAdMobCallback() {
        loader.onAdReceived(mock())
        loader.onAdClicked()

        verify(mediationNativeAdCallback).reportAdClicked()
    }

    @Test
    fun onAdReceivedAndOnAdImpression_ReportToAdMobCallback() {
        loader.onAdReceived(mock())
        loader.onAdImpression()

        verify(mediationNativeAdCallback).reportAdImpression()
    }

    @Test
    fun onAdReceivedAndOnAdLeftApplication_ReportToAdMobCallback() {
        loader.onAdReceived(mock())
        loader.onAdLeftApplication()

        verify(mediationNativeAdCallback).onAdOpened()
        verify(mediationNativeAdCallback).onAdLeftApplication()
    }

    @Test
    fun onAdReceivedAndOnAdClosed_ReportToAdMobCallback() {
        loader.onAdReceived(mock())
        loader.onAdClosed()

        verify(mediationNativeAdCallback).onAdClosed()
    }
}
