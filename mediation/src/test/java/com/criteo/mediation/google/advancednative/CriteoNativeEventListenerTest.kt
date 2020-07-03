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
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.mediation.customevent.CustomEventNativeListener
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class CriteoNativeEventListenerTest {

  @Mock
  private lateinit var adMobListener: CustomEventNativeListener

  private lateinit var listener: CriteoNativeEventListener

  @Before
  fun setUp() {
    MockitoAnnotations.initMocks(this)

    listener = CriteoNativeEventListener(mock(), adMobListener)
  }

  @Test
  fun onAdFailedToReceive_GivenNoFill_ReportToAdMobListener() {
    listener.onAdFailedToReceive(CriteoErrorCode.ERROR_CODE_NO_FILL)

    verify(adMobListener).onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL)
  }

  @Test
  fun onAdFailedToReceive_GivenInternalError_ReportToAdMobListener() {
    listener.onAdFailedToReceive(CriteoErrorCode.ERROR_CODE_INTERNAL_ERROR)

    verify(adMobListener).onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR)
  }

  @Test
  fun onAdClicked_GivenAdMobListener_ReportToIt() {
    listener.onAdClicked()

    verify(adMobListener).onAdClicked()
  }

  @Test
  fun onAdImpression_GivenAdMobListener_ReportToIt() {
    listener.onAdImpression()

    verify(adMobListener).onAdImpression()
  }

  @Test
  fun onAdLeftApplication_GivenAdMobListener_ReportToIt() {
    listener.onAdLeftApplication()

    verify(adMobListener).onAdOpened()
    verify(adMobListener).onAdLeftApplication()
  }

  @Test
  fun onAdClosed_GivenAdMobListener_ReportToIt() {
    listener.onAdClosed()

    verify(adMobListener).onAdClosed()
  }

}